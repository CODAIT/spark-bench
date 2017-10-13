---
layout: page
title: Examples
permalink: /examples/
---

Spark-Bench is best understood by example. The following pages show and explain the configuration files
from the examples included in the distribution. 

<ul>
  {% for page in site.examples %}
    <li>
      <h3>
        <a class="page-link" href="{{ page.url | relative_url }}">{{ page.title | escape }}</a>
      </h3>
    </li>
  {% endfor %}
</ul>

