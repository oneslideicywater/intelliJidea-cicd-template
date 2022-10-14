plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.7.0"
}

group = "com.oneslide"
version = "3.0.1"

repositories {
    mavenCentral()
}
dependencies {

    implementation("org.dom4j:dom4j:2.1.3"){
        exclude(group= "pull-parser")
    }
    implementation("org.jetbrains:marketplace-zip-signer:0.1.8")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */


    ))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("203.8084.24")
        untilBuild.set("")
    }

    signPlugin {
        certificateChain.set("""
-----BEGIN CERTIFICATE-----
MIIFVTCCAz2gAwIBAgIJAKyEQo59N3eCMA0GCSqGSIb3DQEBCwUAMEExCzAJBgNV
BAYTAkNOMQswCQYDVQQIDAJDTjELMAkGA1UEBwwCQ04xCzAJBgNVBAoMAkNOMQsw
CQYDVQQLDAJDTjAeFw0yMjA5MDUwMjMyMjBaFw0yMzA5MDUwMjMyMjBaMEExCzAJ
BgNVBAYTAkNOMQswCQYDVQQIDAJDTjELMAkGA1UEBwwCQ04xCzAJBgNVBAoMAkNO
MQswCQYDVQQLDAJDTjCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAMOb
OgvpujD3tl7Oc/DF0bl46Kh/KfcV/wRuLv0JDE4EY5Aeb+hoCgLtPyB28ZNo5ZXK
AE5X/Y6HsHcjVCVSOxASumzAjsWg7DRfjol1cMfLcoL9YxL3kK1RC7dgVjSXWFkj
b/HqkZPox2JQdZbT8o3tnqGMTXGIwscTsyktNzrf+PgpLSvYNXSnmMA1sNFFB4rv
Dmpzd/tniAgaAwd28tGOHEQhz8WTMzYSPKTSCYaWp5eifoGMDf/RbqDbA/VkH4ip
KYf4VKkmxRArAdeYHIg7LakwF58XEIRVCk11yO55o/zZ3f4PytOLIUgRZIZuFtYP
gFK7+X6PyaC8xNEX/zmqoTYAzrQ0Rf+2Lx+Hwya1PmsVgQtCi11iuoL2GqFJPin0
nIBAumckOl0pjiTTwhTiiGZ4X1Aq5fYWxXwNZ/Q368Hpg45pYutq1fIVOKzuD0tJ
zkiJJdLK3tCKC1nfh3xLHBSZY2nypby61elVn7pBf2U9jG4oyhkQfo4r47c61GY5
CeRvSZredXKc2PUyBVcPSBlGN4bpBM2QAQ/wpHE3OR2oTRxD/HGStq6v2zn4TJZH
LmTWyI4jsFbBvIyR4FZZsOYxuRzAPJSNq7yIB7AVaGMdANiugYnHb5tX8sNH0FgS
5+zC9M64pzGCiTkodFMqLW9XP4rWbLa18VRjjsqlAgMBAAGjUDBOMB0GA1UdDgQW
BBReuYmOaIFJmU0Qe2cyVElZOfnZWzAfBgNVHSMEGDAWgBReuYmOaIFJmU0Qe2cy
VElZOfnZWzAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4ICAQAx5Ar+uRdX
l27U7rwJCtHz2lEz/h01qFs/+0/0vMHjdT0lly/9XmnUBPgYnYPpQb5rlGUXrGEx
hTEk8ApDv0PrzjS6PmOJ1ta/WoZ3p+DB3Yu1zFVSPcv6cuW9OsBTyCAfXFJMD70L
SsHPKhak2kylXGyrEoBZjxmT7uMKsi28WsGem99H8JZJAhz3vcYcr4/bWuU2wiCo
jt4s773UUtcoqf96OGneKOtwtHdVzyrSX0FpVIYeW9inAV1hCMpn0eQGR3Kl9uEg
EJMucEIpepyCHKIFzpuaXLxE0gHXxEZjwNgdLJkkAz46j+WV8RQoL5i5IurMNaV/
UJRiSo2WjEPJpco8mdgOrp6TAN3LvZn/xs/kdPXbi90WWNV3i8wduacLh9qIDJ5w
Y7GCTqmg9NRHUF4uDUIffK6JyB/aCpi6c5SOFwBy5ajM5SZhMaQFSbSxDoEXzqQe
PvCqfbmsXIxukIIdinBbCCwkp4QwOgpFyI/Cw5XMfMaJV6mKbdWh3mvavpeO+ghE
P7QM3pijd1acSd4IybKA8PQtvJa+d8h7obarcb5L8kLt8s+CAQDJjtfnFpNOrqWZ
w1cSR/FdWobRJZPfjvcbk/mynm9x4/VqvEu4AC/EzsDKTXeRu0krAQiBj9H9C8h6
ELj6k4KcBtjRx9h/xGbm3IsiQWr3d9jncQ==
-----END CERTIFICATE-----
  """.trimIndent())

        privateKey.set("""

-----BEGIN ENCRYPTED PRIVATE KEY-----
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
JkXR4B/HnNnThpRsF61jkznPWA==
-----END ENCRYPTED PRIVATE KEY-----
  """.trimIndent())

        password.set("123456")
    }

    publishPlugin {
        token.set(System.getenv("perm:b25lc2xpZGVpY3l3YXRlcg==.OTItNjY1NA==.KegFFcFnVyurttwAKCNgaIlG3CMF49"))
    }
}
