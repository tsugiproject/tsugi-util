
What is it?
-----------

Tsugi-util contains a set of utility classes to aid  in the development
of BasicLTI consumers and providers. They deal with much of the heavy lifting
and make the process more opaque to the developer.

This code originated in the Sakai project as "basiclti-util" - it was always
supposed to be "Sakai-free" and many Java projects used that Sakai artifact.
As of May, 2017 - this is pulled into the Tsugi project for future maintenance
and development as it is really the "level-1" of the Tsugi Java 
application stack.

This new independent artifact makes much more sense for non-Sakai Java projects.
This also allows a simpler way to contribute new code and bug fixes to this
library.

Using tsugi-util
----------------

You can add the following to your pom.xml:

    <dependency>
        <groupId>org.tsugi</groupId>
        <artifactId>tsugi-java</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>

    <dependency>
       <groupId>org.tsugi</groupId>
       <artifactId>tsugi-util</artifactId>
       <version>0.1-SNAPSHOT</version>
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

Releasing tsugi-util to Sonatype
--------------------------------

Here is a sample `~.m2/settings.xml`:

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

    mvn compile install deploy

Check results of the deploy at:

    https://oss.sonatype.org/#nexus-search;quick~tsugi-util

After a while the files migrate to:

    https://oss.sonatype.org/content/repositories/snapshots/org/tsugi/


