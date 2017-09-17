#!/bin/bash
if [ "$REPO_CFG" = "" ]; then
    echo "REPO_CFG must be set"
    exit 1
fi
if [ ! -f "$REPO_CFG" ]; then
    echo "$REPO_CFG does not exist"
    exit 1
fi
REPO=`dirname $REPO_CFG`
CACHE=$REPO/ocr/cache
INVOICES=$HOME/coldstream/invoices
if [ ! -d $CACHE ]; then
    echo "Not found: $CACHE"
    exit 1
fi

for f in $INVOICES/*/*
do
    f_xml="${f}.xml"
    if [ -f "${f_xml}" ]; then
        echo "#### Found scan file: $f_xml"
        sha256=`sha256sum -b "$f" |cut -d ' ' -f 1`
        target=${CACHE}/$sha256
        cp "$f_xml" $target
        echo "Copied to: $target"
    #else
    #   echo "NOOO: ${f}"
    fi
done
