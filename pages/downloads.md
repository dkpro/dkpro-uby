---
layout: page-fullwidth
title: "Downloads"
permalink: "/downloads/"
---

{% assign stable = (site.data.releases | where:"status", "stable" | first) %}

## Maven

{{ site.title }} is availble via the Maven infrastructure.

{% highlight xml %}
<properties>
  <dkpro.uby.version>{{ stable.version }}</dkpro.uby.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>{{ stable.groupId }}<groupId>
      <artifactId>{{ stable.artifactId }}</artifactId>
      <version>${dkpro.uby.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>{{ stable.groupId }}</groupId>
    <artifactId>de.tudarmstadt.ukp.uby.lmf.api-asl</artifactId>
  </dependency>
</dependencies>
{% endhighlight xml %}

A full list of artifacts is available from [Maven Central][1]! 
  
## Sources

Get the sources from [GitHub](https://github.com/dkpro/dkpro-uby).

## Databases

We provide dumps of UBY databases for [download](http://uby.ukp.informatik.tu-darmstadt.de/uby). 

{% comment %}
Get more information on the database dumps we offer on [http://code.google.com/p/uby/wiki/DbImportTutorial this Wiki page].
{% endcomment %}


[1]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22{{ stable.groupId }}%22%20AND%20v%3A%22{{ stable.version }}%22


