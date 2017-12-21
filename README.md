# ODL-Moon-Authz

A demo shows how to add a new filter in odl-aaa in order to upstream all the authorization to another security manager, the Moon platform.

## Overview
- /aaa coorespond midification on odl-aaa source code here:
```bash
    git clone https://git.opendaylight.org/gerrit/aaa
```
- /karaf coorespond midification on odl karaf configuration to acivate the Moon authorization

## Moon Docker
In place of the moonservlet, a [Moon_bouchon](https://git.opnfv.org/moon/tree/moonv4/moon_bouchon/README.md) docker is developed as a fake interface to the Moon platform.
```bash
docker pull wukongsun/moon_bouchon:v1.0
sudo docker run --rm -it --name moon -p 31002:31002 wukongsun/moon_bouchon:v1.0
```

## MoonAuthorizationFilter
Add the file: `/aaa/aaa-shiro/impl/src/main/java/org/opendaylight/aaa/impl/shiro/filters/`[MoonAuthorizationFilter.java](/aaa/aaa-shiro/impl/src/main/java/org/opendaylight/aaa/impl/shiro/filters/MoonAuthorizationFilter.java)

Add package dependicies in `/aaa/aaa-shiro/impl/`[pom.xml](/aaa/aaa-shiro/impl/pom.xml)
```xml
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.7</version>
        </dependency>
    <Import-Package>
        com.sun.jersey.*,
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

    <main>
        <pair-key>moonAuthorization.url</pair-key>
        <pair-value>http://localhost:31002</pair-value>
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

The authorization will be controled by Moon docker.
