package list.shivsamarth_mbs;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalDigitsInputFilter implements InputFilter {
/*
Pattern mPattern;

public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
    mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
	// mPattern=Pattern.compile("[0-9]," + (digitsBeforeZero-1) + ".[0-9]," + (digitsAfterZero-1));
}

@Override
public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
{

        Matcher matcher=mPattern.matcher(dest);       
        if(!matcher.matches())
            return "";
        return null;
    }
*/
	private final int decimalDigits;

	  /**
	   * Constructor.
	   * 
	   * @param decimalDigits maximum decimal digits
	   */
	  public DecimalDigitsInputFilter(int decimalDigits) {
	    this.decimalDigits = decimalDigits;
	  }

	  @Override
	  public CharSequence filter(CharSequence source,
	      int start,
	      int end,
	      Spanned dest,
	      int dstart,
	      int dend) {


	    int dotPos = -1;
	    int len = dest.length();
	    for (int i = 0; i < len; i++) {
	      char c = dest.charAt(i);
	      if (c == '.' || c == ',') {
	        dotPos = i;
	        break;
	      }
	    }
	    if (dotPos >= 0) {

	      // protects against many dots
	      if (source.equals(".") || source.equals(","))
	      {
	          return "";
	      }
	      // if the text is entered before the dot
	      if (dend <= dotPos) {
	        return null;
	      }
	      if (len - dotPos > decimalDigits) {
	        return "";
	      }
	    }

	    return null;
	  }


}
