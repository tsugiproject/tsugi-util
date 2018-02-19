What is it?
-----------

[![Apereo Incubating badge](https://img.shields.io/badge/apereo-incubating-blue.svg?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAABmJLR0QA%2FwD%2FAP%2BgvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH4QUTEi0ybN9p9wAAAiVJREFUKM9lkstLlGEUxn%2Fv%2B31joou0GTFKyswkKrrYdaEQ4cZAy4VQUS2iqH%2BrdUSNYmK0EM3IkjaChnmZKR0dHS0vpN%2FMe97TIqfMDpzN4XkeDg8%2Fw45R1XNAu%2Fe%2BGTgAqLX2KzAQRVGytLR0jN2jqo9FZFRVvfded66KehH5oKr3dpueiMiK915FRBeXcjo9k9K5zLz%2B3Nz8EyAqX51zdwGMqp738NSonlxf36Cn7zX9b4eYX8gSBAE1Bw9wpLaW%2BL5KWluukYjH31tr71vv%2FU0LJ5xzdL3q5dmLJK7gON5wjEQizsTkFMmeXkbHxtHfD14WkbYQaFZVMzk1zfDHERrPnqGz4wZ1tYfJ5%2FPMLOYYW16ltrqKRDyOMcYATXa7PRayixSc4%2FKFRhrqjxKGIWVlZVQkqpg1pYyvR%2BTFF2s5FFprVVXBAAqq%2F7a9uPKd1NomeTX4HXfrvZ8D2F9dTSwWMjwywueJLxQKBdLfZunue0Mqt8qPyMHf0HRorR0ArtbX1Zkrly7yPNnN1EyafZUVZLJZxjNLlHc%2BIlOxly0RyktC770fDIGX3vuOMAxOt19vJQxD%2BgeHmE6liMVKuNPawlZ9DWu2hG8bW1Tuib0LgqCrCMBDEckWAVjKLetMOq2ZhQV1zulGVFAnohv5wrSq3tpNzwMR%2BSQi%2FyEnIl5Ehpxzt4t6s9McRdGpIChpM8Y3ATXbkKdEZDAIgqQxZrKo%2FQUk5F9Xr20TrQAAAABJRU5ErkJggg%3D%3D)](https://www.apereo.org/content/projects-currently-incubation)

Tsugi-util contains a set of utility classes to aid  in the development
of BasicLTI consumers and providers. They deal with much of the heavy lifting
and make the process more opaque to the developer.

This code originated in the Sakai project as "basiclti-util" - it was always
designed to be "Sakai-free" and many Java projects used that Sakai artifact.
As of May, 2017 - this is pulled into the Tsugi project for future maintenance
and development as it is really the "level-1" of the Tsugi Java 
application stack.

This new independent repo for this artifact makes much more sense for non-Sakai Java projects.
This also allows a simpler way to contribute new code and bug fixes to this
library.

Compiling a Local Copy
----------------------

There are 'deploy to SonaType' instructions below.  But if you want to compile and 
develop locally, use this command:

    mvn clean compile install


Running Unit Tests
------------------

Use the command 

    mvn test

Bringing in Changes from Sakai
------------------------------

This code also lives (for now) in the Sakai tree to compare them:

    bash compare.sh | sh -vx

To pull the changes in:

    bash update.sh

You probably want to hand-run the `cp` commands.

Using tsugi-util
----------------

You can add the following to your pom.xml:

    <dependency>
        <groupId>org.tsugi</groupId>
        <artifactId>tsugi-java</artifactId>
        <version>0.3-SNAPSHOT</version>
    </dependency>

    <dependency>
       <groupId>org.tsugi</groupId>
       <artifactId>tsugi-util</artifactId>
       <version>0.3-SNAPSHOT</version>
    </dependency>

If you need to enable snapshot downloading add the following to your
pom.xml:

    <repositories>
        <repository>
            <id>sonatype-nexus-snapshots</id>
            <name>Sonatype Nexus Snapshots</name>
            <url> https://oss.sonatype.org/content/repositories/snapshots </url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

This way when maven sees a SNAPSHOT version, it can find where to download it from.

You can see this all in action in the Tsugi Java Servlet:

    https://github.com/tsugiproject/tsugi-java-servlet

Tsugi Architecture
------------------

Tsugi generally has a two-layer architecuture for building APIs.  The lowest
level API simply wraps the protocols.   This "tsugi-util" code is the low level API.

The second level API is opinionated, with conventions for data tables and sessions.
As a result the method signatures for the second level Tsugi APIs are much simpler.
The second-level API for tsugi-java is here:

    https://github.com/tsugiproject/tsugi-java

The tsugi-java-servlet using both the tsugi-util and tsugi-java libraries.

    https://github.com/tsugiproject/tsugi-java-servlet

Releasing tsugi-util to Sonatype
--------------------------------

Here is a sample `~/.m2/settings.xml`:

    <settings>
      <servers>
        <server>
          <id>ossrh</id>
          <username>drchuck</username>
          <password>?secret!</password>
        </server>
      </servers>
    <profiles>
      <profile>
         <id>allow-snapshots</id>
            <activation><activeByDefault>true</activeByDefault></activation>
         <repositories>
           <repository>
             <id>snapshots-repo</id>
             <url>https://oss.sonatype.org/content/repositories/snapshots</url>
             <releases><enabled>false</enabled></releases>
             <snapshots><enabled>true</enabled></snapshots>
           </repository>
         </repositories>
       </profile>
    </profiles>
    </settings>

Once you have settings set up, 

    mvn clean javadoc:jar compile install deploy -Dgpg.passphrase=Whatever
    mvn clean #afterwards

Check results of the deploy at:

    https://oss.sonatype.org/#nexus-search;quick~tsugi-util

After a while the files migrate to:

    https://oss.sonatype.org/content/repositories/snapshots/org/tsugi/

Doing A Numbered Release
------------------------

    git checkout -b 0.2.x
    change pom.xml version 0.2.1
    mvn clean javadoc:jar compile install deploy -Dgpg.passphrase=Whatever
    git commmit -a
    git push origin 0.2.x

    git checkout master
    change pom.xml version 0.3-SNAPSHOT
    mvn clean javadoc:jar compile install deploy -Dgpg.passphrase=Whatever
    git commmit -a
    git push

Check results of the deploy at:

    https://oss.sonatype.org/#nexus-search;quick~tsugi-util

After a while the files migrate to:

    https://oss.sonatype.org/content/repositories/snapshots/org/tsugi/

Key Making Notes
----------------

    m-c02m92uxfd57:tsugi-util csev$ gpg --gen-key
    gpg (GnuPG/MacGPG2) 2.0.30; Copyright (C) 2015 Free Software Foundation, Inc.
    This is free software: you are free to change and redistribute it.
    There is NO WARRANTY, to the extent permitted by law.

    Please select what kind of key you want:
    (1) RSA and RSA (default)
    (2) DSA and Elgamal
    (3) DSA (sign only)
    (4) RSA (sign only)
    Your selection? 1
    RSA keys may be between 1024 and 4096 bits long.
    What keysize do you want? (2048) 
    Requested keysize is 2048 bits   
    Please specify how long the key should be valid.
         0 = key does not expire
      <n>  = key expires in n days
      <n>w = key expires in n weeks
      <n>m = key expires in n months
      <n>y = key expires in n years
    Key is valid for? (0) 0
    Key does not expire at all
    Is this correct? (y/N) y

    GnuPG needs to construct a user ID to identify your key.

    Real name: Charles Severance
    Email address: drchuck@gmail.com
    Comment: For Sonatype           
    You selected this USER-ID:
    "Charles Severance (For Sonatype) <drchuck@gmail.com>"

    Change (N)ame, (C)omment, (E)mail or (O)kay/(Q)uit? O
    You need a Passphrase to protect your secret key.    

    We need to generate a lot of random bytes. It is a good idea to perform
    some other action (type on the keyboard, move the mouse, utilize the
    disks) during the prime generation; this gives the random number
    generator a better chance to gain enough entropy.
    We need to generate a lot of random bytes. It is a good idea to perform
    some other action (type on the keyboard, move the mouse, utilize the
    disks) during the prime generation; this gives the random number
    generator a better chance to gain enough entropy.
    gpg: key BCDACC58 marked as ultimately trusted
    public and secret key created and signed.

    gpg: checking the trustdb
    gpg: 3 marginal(s) needed, 1 complete(s) needed, PGP trust model
    gpg: depth: 0  valid:   3  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 3u
    gpg: next trustdb check due at 2018-08-19
    pub   2048R/BCDACC58 2016-07-26
      Key fingerprint = 0A6A FE01 ...
    uid       [ultimate] Charles Severance (For Sonatype) <drchuck@gmail.com>
    sub   2048R/9B8D98F2 2016-07-26

    m-c02m92uxfd57:tsugi-util csev$ gpg --keyserver hkp://pool.sks-keyservers.net --send-keys BCDACC58
    gpg: sending key BCDACC58 to hkp server pool.sks-keyservers.net

