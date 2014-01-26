#!/bin/bash

if [ -z "$1" -o -z "$2" ]; then
    echo "USAGE: $0 <input-image> <xhdpi-size>"
    echo "       Adaptively resizes given image to mdpi, hdpi, xhdpi and xxhdpi sizes."
    echo "       Resulting images are renamed to ic_*.png and placed in the corresponding directories."
    echo "OPTIONS:"
    echo "  <input-image>  Input image to read, should not be in one of the *dpi/ directories"
    echo "  <xhdpi-size>   Size of the xhdpi image in pixel, should be in imagemagick format,"
    echo "                 e.g. 72x72 gives maximum values of width and height and preserves aspect ratio"
    exit 0
fi


die() {
    echo "ERROR: $1"
    exit 1
}


# from the Android developer docs
#
# xxhdpi: 3.0
# xhdpi:  2.0
# hdpi:   1.5
# tvdpi:  1.33 (TVs only)
# mdpi:   1.0 (baseline)
# ldpi:   0.75
#
# starting from xhdpi:
#
# xxhdpi: 1.5
# xhdpi:  1.0 (baseline)
# hdpi:   0.75
# mdpi:   0.5

SIZE_XXHDPI=$(echo "$2" | perl -pe 's/(\d+)/{ sprintf("%.0f", $1 * 1.5) }/ge')
SIZE_XHDPI="$2"
SIZE_HDPI=$(echo "$2" | perl -pe 's/(\d+)/{ sprintf("%.0f", $1 * 0.75) }/ge')
SIZE_MDPI=$(echo "$2" | perl -pe 's/(\d+)/{ sprintf("%.0f", $1 * 0.5) }/ge')


SRC_IMG="$1"
if echo "$SRC_IMG" | egrep -q "drawable-[a-z]+dpi/" ; then
    die "$SRC_IMG seems to reside in one of the *dpi/ directories."
fi
if [[ ! -r "$SRC_IMG" ]]; then
    die "Source file does not exist"
fi


TGT_IMG_NAME=$(basename "$SRC_IMG" | /bin/sed -r -e 's/^/ic_/' -e 's/^ic_ic_/ic_/g' -e 's/\.[a-zA-Z]+$/.png/')

SRC_PIXELS=$(identify -ping -format "%wx%h\n" "$SRC_IMG" | perl -pe 's/^(\d+)x(\d+)$/{ $1*$2 }/e')
TGT_PIXELS=$(echo "$SIZE_XXHDPI" | perl -pe 's/^(\d+)x(\d+)$/{ $1*$2 }/e')
if [[ $SRC_PIXELS -lt $TGT_PIXELS ]]; then
    echo "WARNING: The source image is smaller ($(identify -ping -format "%wx%h\n" "$SRC_IMG")) than the resulting xxhdpi resolution ($SIZE_XXHDPI)";
    echo "Press enter if you still want to continue, abort with CTRL-C"
    read ANS1
fi

# do_convert <input-file> <density-name> <target-filename> <target-resolution>
do_convert() {
    if [[ ! -d  "res/drawable-$2" ]]; then
        mkdir -p "res/drawable-$2" 
    fi
    if [[ -r "res/drawable-$2/$3" ]]; then
        echo "WARNING: Target file 'res/drawable-$2/$3' exists."
    fi
    convert -adaptive-resize "$4" -antialias "$1" "res/drawable-$2/$3" || die "Convert failed"
}

while read NAME RES; do
    echo
    echo -e "$NAME:\t$RES"
    do_convert "$SRC_IMG" "$NAME" "$TGT_IMG_NAME" "$RES"
done <<< "xxhdpi $SIZE_XXHDPI
xhdpi $SIZE_XHDPI
hdpi $SIZE_HDPI
mdpi $SIZE_MDPI"


