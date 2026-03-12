# Maintainer: Maheswara660 <https://github.com/Maheswara660>
pkgname=app-dock
pkgver=1.0.0
pkgrel=1
pkgdesc="App Dock built with Compose Multiplatform"
arch=('x86_64')
url="https://github.com/Maheswara660/App-Dock"
license=('GPL')
depends=('java-runtime>=17')
makedepends=('java-environment>=17' 'git')
source=("${pkgname}::git+${url}.git")
sha256sums=('SKIP')

build() {
    cd "${srcdir}/${pkgname}"
    ./gradlew :desktopApp:packageDeb
}

package() {
    cd "${srcdir}/${pkgname}"
    
    # Locate the generated .deb file
    local deb_file=$(ls desktopApp/build/compose/binaries/main/deb/*.deb | head -n 1)
    
    # Extract the data archive from the debian package
    ar x "$deb_file"
    
    # Extract the data archive to the package directory
    if [ -f data.tar.zst ]; then
        tar -xf data.tar.zst -C "${pkgdir}"
    elif [ -f data.tar.xz ]; then
        tar -xf data.tar.xz -C "${pkgdir}"
    elif [ -f data.tar.gz ]; then
        tar -xf data.tar.gz -C "${pkgdir}"
    fi
}
