/*@props
document.dynamic.userdefined.DDP_HASH_VALUE=hello you too
*/

import java.util.Properties;
import java.io.InputStream;
import java.security.MessageDigest;
import java.math.BigInteger;


for( int i = 0; i < dataContext.getDataCount(); i++ ) {
  InputStream is = dataContext.getStream(i);
  Properties props = dataContext.getProperties(i);

  // Get dyn doc prop to hash
  String input_string = props.getProperty("document.dynamic.userdefined.DDP_HASH_VALUE");

  MessageDigest digest = MessageDigest.getInstance("MD5") ;
  digest.update(input_string.bytes); 
  output = new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0') ;

  // Update dyn doc prop
  props.setProperty("document.dynamic.userdefined.DDP_HASH_VALUE", output);

  dataContext.storeStream(is, props);
}
