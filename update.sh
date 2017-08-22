#! /bin/bash
diff -rq /Users/csev/dev/sakai-scripts/trunk/basiclti/tsugi-util . | grep '.java differ$' | awk '{ print "cp ", $2, $4 }'
