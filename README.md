# ODL-Moon-Authz

A demo shows how to add a new filter in odl-aaa in order to upstream all the authorization to another security manager.

## Overview
Like the [REST-Shiro-demo](https://github.com/WangYuchenSJTU/REST-Shiro-demo), it contains a SDA provider servlet and a consumer:
- /moonservlet provides security data access by responding HTTP POST request
- /aaa coorespond midification on odl-aaa source code here:
```bash
    git clone https://git.opendaylight.org/gerrit/aaa
```
- /karaf coorespond midification on odl karaf configuration to acivate the Moon authorization

## Moonservlet
This servlet is realized by slightly modify the shiroservlet in [REST-Shiro-demo](https://github.com/WangYuchenSJTU/REST-Shiro-demo).

Deploy this servlet for example in tomcat at http://localhost:8081/shiroservlet-1.0-SNAPSHOT/ShiroServlet

## MoonAuthorizationFilter
Add the file: `/aaa/aaa-shiro/impl/src/main/java/org/opendaylight/aaa/impl/shiro/filters/`[MoonAuthorizationFilter.java](/aaa/aaa-shiro/impl/src/main/java/org/opendaylight/aaa/impl/shiro/filters/MoonAuthorizationFilter.java)

Add two package dependicies in `/aaa/aaa-shiro/impl/`[pom.xml](/aaa/aaa-shiro/impl/pom.xml)
```xml
    <Import-Package>
        com.sun.jersey.*,
        org.json.*,
    </Import-Package>
```

## Configuration

Modify the configuration file as: `karaf/etc/opendaylight/datastore/initial/config/`[aaa-app-config.xml](/karaf/etc/opendaylight/datastore/initial/config/aaa-app-config.xml)
```xml
    <!--moonAuthorization filter for REST endpoints-->
    <main>
        <pair-key>moonAuthorization</pair-key>
        <pair-value>org.opendaylight.aaa.shiro.filters.MoonAuthorizationFilter</pair-value>
    </main>

    <!--moonAuthorization use case -->
    <urls>
        <pair-key>/modules</pair-key>
        <pair-value>authcBasic, moonAuthorization</pair-value>
    </urls>
```

## Test
Launch an odl karaf with restconf for example at 
```bash
    git clone https://git.opendaylight.org/gerrit/netconf
```
Install restconf:
```bash
    feature:install odl-restconf
```
Then access page:
http://localhost:8181/restconf/modules

The authorization will be contoled by Moonservlet.
