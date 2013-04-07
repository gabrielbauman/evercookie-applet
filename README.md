# Java Applet Persistence for Evercookie

## What's this?

A Java applet implementing a storage mechanism for Evercookie that uses
several methods to store persistent cookie data in a browser. 

evercookie-applet was written by [Gabriel Bauman][6] and binaries are included
in the [official Evercookie distribution][4]. You can find out more about
Evercookie [here][3].

## How does it work?
 
Evercookie.js injects this applet into the DOM of a page. The applet attempts
to use the [JNLP PersistenceService][0] to store values for Evercookie. For
good measure, it also attempts to use A known exploit for [CVE-2013-0422][1]
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
as you browse the net. Or, remove Java entirely.

One of Evercookie's other methods will probably still work against you, though.

## I know applets, how can I contribute?

Fork it on [GitHub][5] or [Bitbucket][4]. I accept pull requests that make
sense and aren't destructive or overly malicious.

[0]: http://docs.oracle.com/javase/1.5.0/docs/guide/javaws/jnlp/index.html
[1]: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2013-0422
[2]: https://github.com/samyk/evercookie
[3]: http://samy.pl/evercookie/
[4]: https://bitbucket.org/gabrielbauman/evercookie-applet
[5]: https://github.org/gabrielbauman/evercookie-applet
[6]: http://gabrielbauman.com
