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
        <version>0.2-SNAPSHOT</version>
    </dependency>

    <dependency>
       <groupId>org.tsugi</groupId>
       <artifactId>tsugi-util</artifactId>
       <version>0.2-SNAPSHOT</version>
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

    mvn clean compile install deploy -Dgpg.passphrase=Whatever clean

Check results of the deploy at:

    https://oss.sonatype.org/#nexus-search;quick~tsugi-util

After a while the files migrate to:

    https://oss.sonatype.org/content/repositories/snapshots/org/tsugi/


