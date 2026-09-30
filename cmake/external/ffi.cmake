include(ExternalProject)

ExternalProject_Add(ep_ffi
        SOURCE_DIR ${THIRD_PARTY_SOURCE_PATH}/libffi
        PATCH_COMMAND git clean -dfx
        BINARY_DIR ${CMAKE_BINARY_DIR}/sysroot/src/ep_ffi-build
        # Generate the configure script in the source checkout, then build outside
        # it. libffi's in-source cross-compile wrapper is unreliable on bind mounts.
        CONFIGURE_COMMAND cd <SOURCE_DIR> && ./autogen.sh
            && cd <BINARY_DIR> && <SOURCE_DIR>/configure ${HOST_FLAG}
            --disable-exec-static-tramp
            --disable-multi-os-directory
            --disable-static --enable-pax_emutramp
            --prefix ${CMAKE_BINARY_DIR}/sysroot
        BUILD_COMMAND ${Make_EXECUTABLE}
        INSTALL_COMMAND ${Make_EXECUTABLE} install
        USES_TERMINAL_DOWNLOAD true
        USES_TERMINAL_BUILD true
)
