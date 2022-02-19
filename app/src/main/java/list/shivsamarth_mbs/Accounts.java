package list.shivsamarth_mbs;

public class Accounts {
	
	// SUCCESS^<BRANCH_CD#SCH_CD#ACNT_NO#ACNT_TYPE#NAME>$....^FLAG#<cust name>
	
	//int amt,bal;
	String str = "", str2 = "";
	int accNo;
	String brCd,schCd,accNm,accType,accHolderName,balace;
	
	public Accounts(String record)
	{
		System.out.println("in Accounts() constructor..."+record+"<======");
		accNo=0;
		brCd=schCd="";
		accNm="";
		accType="";
		balace="";
		String str2[] = record.split("#");
		/*
		 * <br_cd>#<schm_cd>#<acc_type>#<acc_no>#<acc_name>
			5#101#SB#1#KADEKAR KAVITA  KIRAN,-,
			5#101#SB#2#KADEKAR KAVITA  KIRAN,-,
			5#101#SB#3#Mrs. KADEKAR KAVITA KIRAN,-,
			5#101#SB#6#KADEKAR DIGAMBAR HARI / KAVITA KIRAN,-,
			5#101#SB#7#DESHPANDE JAGGANATH SHANKAR / KADEKAR KAVITA K.,-,
		*/
		for (int j = 0; j < str2.length; j++) {
			// SUB1,SCHEM1,1010101010,CURENT,OMKAR KUSHTE
			brCd = str2[0];
			schCd = str2[1];
			accNo = Integer.parseInt(str2[3]);
			accType = str2[2];
			accHolderName = str2[4];
			balace=str2[9];
		}
		
		System.out.println("brCd------"+brCd+"schCd------"+schCd+"accNo------"
				+accNo+"accType------"+accType+"accHolderName------"+accHolderName+
				"balace=="+balace);
	}
	
	public String getBalace() {
		return balace;
	}

	public void setBalace(String balace) {
		this.balace = balace;
	}

	String getBrCd()
	{
		return brCd;
	}
	int getAccNo()
	{
		return accNo;
	}
	String getSchCd()
	{
		return schCd;
	}
	
	String getAccNm()
	{
		return accNm;
	}
	String getAccType()
	{
		return accType;
	}
	String getHolderName()
	{
		return accHolderName;
	}
	
}
