{ pkgs ? import <nixpkgs> { } }:

let
  unstable = import (fetchTarball https://github.com/NixOS/nixpkgs/archive/nixos-unstable.tar.gz) { };
  toolchains = [ unstable.temurin-bin-21 ];
in

pkgs.mkShell {
  nativeBuildInputs = [
    (unstable.gradle.overrideAttrs (curr: old: {
      fixupPhase = old.fixupPhase + ''
        cat > $out/lib/gradle/gradle.properties <<EOF
        org.gradle.java.installations.paths=${pkgs.lib.concatStringsSep "," toolchains}
        EOF
      '';
    }))
  ];
}
