package inventoryManager;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.rmi.runtime.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;

/*
*    TOC19 is a simple program to run TOC payments within a small group. 
*    Copyright (C) 2014  Jarrah Gosbell
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Author: Jarrah Gosbell
 * Student Number: z5012558
 * Class: PersonDatabase
 * Description: This program will allow for the input and retreval of the person database and will set the limits of the database.
 */

class Settings {
	/**
	 * The properties object which is used to interact with the properties file
	 */
	private final Properties properties = new Properties();
	/**
	 * the path to the properties file which contains the settings
	 */
	private final String propFileName = Compatibility.getFilePath("inventoryManager.properties");
	/**
	 * an input stream which is used to access the properties file
	 */
	private InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

    private static final char[] PASSWORD = "saoenuthrac,.".toCharArray();
    private static final byte[] SALT = {
            (byte) 0xde, (byte) 0xa3, (byte) 0x10, (byte) 0x15,
            (byte) 0xde, (byte) 0xa3, (byte) 0x10, (byte) 0x15,
    };
	/**
	 * Create an instance of the settings class from which to read settings from.
	 */
	public Settings() {
		if (inputStream != null) return;

		try {
			if (inputStream == null) {
				inputStream = new FileInputStream(String.valueOf(Paths.get(propFileName)));
			}
			if (inputStream == null) {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		} catch (FileNotFoundException e) {
		//	Log.print(e);
		}
	}

	/**
	 * Get the settings for the person database, specifically the location to store the database
	 *
	 * @return The location in which the database is stored. This is checked for compatibility against the running OS
	 * @throws FileNotFoundException if the settings file is not in the location it should be.
	 */
	public final String personSettings() throws FileNotFoundException {
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
			//	Log.print("property file '" + propFileName + "' not " +
               //         "found in the classpath");
			}
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		String output;
		output = properties.getProperty("personDatabaseLocation");
		output = Compatibility.getFilePath(output);
		return output;
	}

	/**
	 * Get the settings for the product datasbase, specifically the location to store the database in.
	 *
	 * @return The location in which the database is stored. This is checked for compatibility against the running OS
	 * @throws FileNotFoundException If the settings file is not in the location it should be.
	 */
	public final String productSettings() throws FileNotFoundException {
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
			//	Log.print("property file '" + propFileName + "' not " +
              //          "found in the classpath");
			}
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		String output;
		output = properties.getProperty("productDatabaseLocation");
		output = Compatibility.getFilePath(output);
		return output;
	}

	/**
	 * Get the settings for the interface. Specifically the horizontal size, vertical size, (both in pixels) and the text size
	 *
	 * @return A string array with the horizontal size, vertical size and textsize.
	 * @throws FileNotFoundException If the settings file is not in the location it should be.
	 */
	public final String[] interfaceSettings() throws FileNotFoundException {
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
			//	Log.print("property file '" + propFileName + "' not " +
                  //      "found in the classpath");
			}
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		String[] output = new String[3];
		output[0] = properties.getProperty("horizontalSize");
		output[1] = properties.getProperty("verticalSize");
		output[2] = properties.getProperty("textSize");
		return output;
	}
	/**
	 * Get the settings for the error log. Specifically the location of it's storage
	 * @return A string with the location of the log. This is checked for compatibility against the running OS
	 * @throws FileNotFoundException If the settings file is not in the location it should be.
	 */
	public final String logSettings() throws FileNotFoundException {
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
			//	Log.print("property file '" + propFileName + "' not " +
                //        "found in the classpath");
			}
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		String output;
		output = properties.getProperty("logFileLocation");
		output = Compatibility.getFilePath(output);
		return output;
	}
    public final String[] SQLInterfaceSettings() throws FileNotFoundException
    {
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            }
            catch(IOException e) {
                System.out.print("property file '" + propFileName + "' not found in the classpath");
            }
        }
        else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String[] output = new String[3];
        output[0] = properties.getProperty("URL");
        output[1] = properties.getProperty("user");
        try {
            output[2] = decrypt(properties.getProperty("password"));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }

    private static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }
}
