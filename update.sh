#! /bin/bash
diff -rq src /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util/src | grep '.java differ$' | awk '{ print "cp ", $4, $2 }'

diff -rq src /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util/src | grep 'Only in /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util/src' | grep -v .DS_Store | sed 's"Only in /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util/src""' | sed 's": "/"' | awk '{ print "cp /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util/src" $1, "src" $1 }'

