/*
 * Copyright (c) 2020. PKI.Tools
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tools.pki.aln;


import android.widget.TextView;

import java.util.UUID;


class StringUtil {
	private static final String TAG = " StringUtil" ;
	private static String currentString = "" ;

	private StringUtil() {
	}

	/** Get the string just passed in
	 * The last method affecting currentString and this method should be in the same thread, otherwise the return value may be incorrect
	 * @return
	 */
	public static String getCurrentString() {
		return currentString == null ? "" : currentString;
	}


	// Get string, return "" if it is null <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<<<<

	/** Get string, return "" for null
	 * @param tv
	 * @return
	 */
	public static String get(TextView tv ) {
		if(tv == null || tv.getText() == null ) {
			return "" ;
		}
		return tv.getText().toString();
	}
	/** Get string, return "" for null
	 * @param object
	 * @return
	 */
	public static String get(Object object ) {
		return object == null ? "" : object.toString();
	}
	/** Get string, return "" for null
	 * @param cs
	 * @return
	 */
	public static String get(CharSequence cs ) {
		return cs == null ? "" : cs.toString();
	}
	/** Get string, return "" for null
	 * @param s
	 * @return
	 */
	public static String get(String s ) {
		return s == null ? "" : s;
	}



	// Get the string after removing the space before and after <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<<
	/*
	 * @param s
	 * @return
	 */
	public static String trim(String s ) {
		return s == null ? "" : s.trim();
	}

	/** deprecated replaced with trim, this is reserved until 17.0
	 */
	public static String getTrimedString(String s ) {
		return trim(s);
	}

	// Get the string after removing the spaces before and after >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>>>>




	// Get string after removing all spaces <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<
	/** Get string with all spaces removed, or "" if null is returned
	 * @param s
	 * @return
	 */
	public static String noBlank(String s ) {
		return get(s).replaceAll(" " , "" );
	}

	// Get the string after removing all spaces >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>>>>


	// Get the length of the string <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<<
	/** Get the length of the string, or null to return 0
	 * @param s
	 * @return
	 */
	public static int length(String s ) {
		return get(s).length();
	}

	/** deprecated is replaced by length, this is reserved until 17.0
	 */
	public static int getLength(String s , boolean trim ) {
		s = trim ? getTrimedString(s) : s;
		return length(s);
	}

	// Get the length of the string >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>


	// Check if the character is empty <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<<<<<<<<<<<<

	/** Determine if the character is empty
	 * trim = true
	 * @param s text to check
	 * @return
	 */
	public static boolean isEmpty(String s ) {
		return isEmpty(s, true );
	}

	/** Determine if the character is empty
	 * @param s
	 * @param trim
	 * @return
	 */
	public static boolean isEmpty(String s , boolean trim ) {
		if(s == null ) {
			return true ;
		}
		if(trim) {
			s = s.trim();
		}
		if(s.length() <= 0 ) {
			return true ;
		}

		currentString = s;

		return false ;
	}
	// Determine if the character is empty >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>>>>>>>>>>>>>>>


  // Determine if the character is not empty <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< <<<<<<<<<<<<<<<<

	/** Determine if the character is not empty
	 * @param s
	 * @param trim
	 * @return
	 */
	public static boolean isNotEmpty(String s , boolean trim ) {
		return ! isEmpty(s, trim);
	}


	public static String random() {
	return 	UUID.randomUUID().toString();
	}

	public static Double toDouble(String valueString) {
		Double latitude = null;
		if (!StringUtil.isEmpty(valueString))
			try {
				latitude = Double.parseDouble(valueString);
			} catch (NumberFormatException e) {
				//not a double
			}
		return latitude;
	}


	// Correct(auto completion, etc.) string >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> >>>>>>>>>>>>>>>>>>>>>>>>>>>>

}