#! /bin/bash
diff -rq . /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util | grep '.java differ$' | awk '{ print "diff ", $2, $4 }'
