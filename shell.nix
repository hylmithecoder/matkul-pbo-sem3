{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [

    # GTK/GNOME libraries for JavaFX/GTK integration
    gtk3
    glib
    pango
    cairo
    atk
    gdk-pixbuf

    # X11 libraries
    xorg.libX11
    xorg.libXext
    xorg.libXrender
    xorg.libXtst
    xorg.libXi
    xorg.libXxf86vm

    # OpenGL & Graphics
    libGL
    fontconfig
    freetype

    # Audio Support
    alsa-lib
  ];

  shellHook = ''
    export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath (with pkgs; [
      gtk3
      glib
      pango
      cairo
      atk
      gdk-pixbuf
      xorg.libX11
      xorg.libXext
      xorg.libXrender
      xorg.libXtst
      xorg.libXi
      xorg.libXxf86vm
      libGL
      fontconfig
      freetype
      alsa-lib
    ])}:$LD_LIBRARY_PATH"
  '';
}
