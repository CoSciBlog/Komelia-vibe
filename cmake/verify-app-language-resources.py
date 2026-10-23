#!/usr/bin/env python3
"""Check the language picker and Appearance translations, optionally inside an APK."""
import argparse
import base64
import json
from pathlib import Path
import re
import xml.etree.ElementTree as ET
import zipfile

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('--apk', type=Path)
args = parser.parse_args()
paths = sorted((root/'komelia-ui/src/commonMain/composeResources').glob('values*/strings.xml'))
keys = ['settings_app_language', 'settings_app_language_system', 'settings_appearance_title',
        'settings_app_theme', 'settings_app_theme_dark', 'settings_app_theme_light',
        'settings_app_theme_darker', 'settings_app_image_card_size',
        'settings_navigation_app_settings', 'settings_navigation_appearance']
assert len(paths) == 7
apk = zipfile.ZipFile(args.apk) if args.apk else None
try:
    checked = 0
    for path in paths:
        elements = ET.parse(path).findall('string')
        strings = {e.attrib['name']: e.text or '' for e in elements}
        assert len(strings) == len(elements), ('Duplicate string key', path)
        for key in keys:
            assert strings.get(key, '').strip(), (path, key)
            assert not re.search(r'%\d+\$[sd]', strings[key]), (path, key)
            if apk:
                line = b'string|' + key.encode() + b'|' + base64.b64encode(strings[key].encode())
                assert any(line in apk.read(n) for n in apk.namelist() if n.endswith('/'+path.parent.name+'/strings.commonMain.cvr')), (path, key)
            checked += 1
    migration = root/'komelia-infra/database/sqlite/src/commonMain/composeResources/files/migrations/app/V15__app_language.sql'
    assert "DEFAULT ''" in migration.read_text()
    if apk:
        assert any(n.endswith('/'+migration.name) for n in apk.namelist()), 'Missing app-language upgrade migration'
    print(json.dumps({'locales':len(paths),'checkedTranslations':checked,'apkResources':'PASS' if apk else 'not requested','result':'PASS'},indent=2))
finally:
    if apk: apk.close()
