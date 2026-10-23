#!/usr/bin/env python3
"""Verify complete UI locale coverage, format tokens, plural forms and optional APK bytes."""
import argparse
import base64
from collections import Counter
import json
from pathlib import Path
import re
import subprocess
import xml.etree.ElementTree as ET
import zipfile

root=Path(__file__).resolve().parents[1]
parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('--apk',type=Path)
parser.add_argument('--report',type=Path)
parser.add_argument('--baseline',help='Git revision whose positional format tokens must be preserved')
args=parser.parse_args()
resources=root/'komelia-ui/src/commonMain/composeResources'
paths=sorted(resources.glob('values*/strings.xml'))
assert len(paths)==7
def load(path):
    items=list(ET.parse(path).getroot())
    mapping={(e.tag,e.attrib['name']):e for e in items}
    assert len(items)==len(mapping),('Duplicate key',path)
    return mapping
source=load(resources/'values/strings.xml')
if args.baseline:
    previous=ET.fromstring(subprocess.check_output(['git','show',args.baseline+':komelia-ui/src/commonMain/composeResources/values/strings.xml'],cwd=root))
    for old in previous:
        current=source[(old.tag,old.attrib['name'])]
        before={'string':old.text} if old.tag=='string' else {e.attrib['quantity']:e.text for e in old}
        after={'string':current.text} if current.tag=='string' else {e.attrib['quantity']:e.text for e in current}
        for quantity,value in before.items():
            assert Counter(re.findall(r'%\d+\$[A-Za-z]',value or ''))==Counter(re.findall(r'%\d+\$[A-Za-z]',after[quantity] or '')),(old.attrib['name'],'Source format changed from baseline')
def tokens(value):
    return Counter(re.findall(r'%\d+\$[A-Za-z]|\$[A-Za-z_][\w.\[\]]*',value or ''))
def normalized(value):
    return re.sub(r'\\([nrt"\'\\])',lambda m:{'n':'\n','r':'\r','t':'\t'}.get(m[1],m[1]),value or '')
apk=zipfile.ZipFile(args.apk) if args.apk else None
checked=0;forms=0;compiled=0;summary=[]
try:
 for path in paths:
    translated=load(path)
    assert translated.keys()==source.keys(),(path,'Missing',source.keys()-translated.keys(),'Extra',translated.keys()-source.keys())
    cvr=None
    if apk:
        names=[n for n in apk.namelist() if n.endswith('/'+path.parent.name+'/strings.commonMain.cvr')]
        assert len(names)==1,(path,names)
        cvr={}
        for line in apk.read(names[0]).decode().splitlines():
            if '|' not in line:continue
            kind,key,payload=line.split('|',2)
            if kind=='string':cvr[(kind,key)]={'string':base64.b64decode(payload).decode()}
            elif kind=='plurals':cvr[(kind,key)]={part.split(':',1)[0].lower():base64.b64decode(part.split(':',1)[1]).decode() for part in payload.split(',')}
    for (kind,key),base in source.items():
        entry=translated[(kind,key)]
        values={'string':entry.text} if kind=='string' else {e.attrib['quantity']:e.text for e in entry}
        expected={'string':base.text} if kind=='string' else {e.attrib['quantity']:e.text for e in base}
        if kind=='plurals':
            assert len(values)==len(list(entry)) and {'one','other'}<=values.keys(),(path,key,'Missing plural forms')
            if path.parent.name=='values-ru':assert {'few','many'}<=values.keys(),(path,key,'Missing Russian forms')
        for quantity,value in values.items():
            assert value and value.strip(),(path,key,quantity,'Empty translation')
            assert not re.search(r'ZXQ\d|XQPH\d|⟦\d|\bTODO\b',value),(path,key,'Translation marker')
            assert tokens(value)==tokens(expected.get(quantity,expected.get('other'))),(path,key,quantity,'Changed format/template tokens')
            if cvr is not None:
                assert cvr[(kind,key)][quantity]==normalized(value),(path,key,quantity,'APK differs from source')
                compiled+=1
            forms+=1
        checked+=1
    summary.append({'locale':path.parent.name,'strings':sum(k[0]=='string' for k in translated),'plurals':sum(k[0]=='plurals' for k in translated)})
 result={'result':'PASS','locales':summary,'checkedResources':checked,'checkedForms':forms,'missingResources':0,'formatAndTemplateTokens':'PASS','pluralForms':'PASS','apkFormsVerified':compiled if apk else 'not requested'}
 if args.report:args.report.write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
 print(json.dumps(result,indent=2))
finally:
 if apk:apk.close()
