[![Build Status](https://api.travis-ci.org/gabrielbauman/evercookie-applet.png?branch=master)](http://travis-ci.org/gabrielbauman/evercookie-applet)

# Java Applet Persistence for Evercookie

## What's this?

A Java applet implementing a storage mechanism for Evercookie that uses
several methods to store persistent cookie data in a browser. 

evercookie-applet was written by [Gabriel Bauman][6] and binaries will soon be
included in the [official Evercookie distribution][2]. You can find out more
about Evercookie [here][3].

## How does it work?
 
Evercookie.js injects this applet into the DOM of a page. The applet attempts
to use the [JNLP PersistenceService][0] to store values for Evercookie. For
good measure, it also attempts to use a known exploit for [CVE-2013-0422][1]
to escape the applet sandbox and write a file to the user's hard drive
containing cookie data.

The PersistenceService method is entirely legitimate and uses official Java
APIs. The exploit method uses an [exploit][1] that is publicly known and has
been patched by Oracle, but it will still work against anyone who hasn't
updated their Java plugin.

## Why would you write this?

Because it's possible, and it shouldn't be. Evercookie already demonstrates
how hard it is to avoid being tracked as you browse the net. This code extends
its capabilities just a little further.

## How can I protect myself?

To protect yourself from this applet, simply keep your Java installation up to
date and don't blindly click "Run" when presented with a Java security warning
as you browse the net.

Be warned, though - *any* Java applet can do what this one does. A game, an
FTP client - all of these can store information on your machine that can later
be used to identify you. Paranoid? Remove the Java plugin entirely.

One of Evercookie's other methods will probably still work against you, though.

## I know applets, how can I contribute?

Fork it on [GitHub][5] or [Bitbucket][4]. 

I accept pull requests that make sense and aren't destructive or overly malicious.

## How to build evercookie-applet

- Check out the source code on your computer.
- Install the [Oracle JDK][7] and the [Apache Maven build system][8].
- Open pom.xml and edit java.home property. Make it point at your JDK.
- Open a terminal or command window and cd to the source code you checked out.
- type "mvn package" and press Enter.
- The jar, jnlp, and test HTML file will be built in the "target" directory

Have fun!

[0]: http://docs.oracle.com/javase/1.5.0/docs/guide/javaws/jnlp/index.html
[1]: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2013-0422
[2]: https://github.com/samyk/evercookie
[3]: http://samy.pl/evercookie/
[4]: https://bitbucket.org/gabrielbauman/evercookie-applet
[5]: https://github.org/gabrielbauman/evercookie-applet
[6]: http://gabrielbauman.com
[7]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[8]: http://maven.apache.org/download.cgi
