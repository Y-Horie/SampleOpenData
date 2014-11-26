package jp.ac.shinshu_u;


public class SetCodelist{

	//部局名称
	//※この部分を要相談
	final static String[] name = {
		"全て",
		"人文",
		"教育",
		"経済",
		"理学",
		"医学",
		"工学",
		"農学",
		"繊維",
		"共通",
		"人修",
		"教修",
		"経修",
		"医修",
		"医博",
		"医系修",
		"医系博",
		"医博前",
		"医博後",
		"理修",
		"工修",
		"繊維修",
		"理工理",
		"理工工",
		"理工繊",
		"工博",
		"総工",
		"農修",
		"法科",
	};

	enum CodeList {
		ALL("全て"),
		L("人文"),
		E("教育"),
		K("経済"),
		S("理学"),
		M("医学"),
		T("工学"),
		A("農学"),
		F("繊維"),
		G("共通"),
		LA("人修"),
		EA("教修"),
		KA("経修"),
		MM("医修"),
		MA("医博"),
		MS("医系修"),
		MH("医系博"),
		MZ("医博前"),
		MK("医博後"),
		SA("理修"),
		TA("工修"),
		FA("繊維修"),
		SM("理工理"),
		TM("理工工"),
		FM("理工繊"),
		TH("工博"),
		ST("総工"),
		AA("農修"),
		JH("法科");

		private String name;
		CodeList(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	public String getCode(String key) {
		String code = "";

		for (CodeList m : CodeList.values()) {
			if(m.getName().equals((key))){
				code = m.toString();
			}
		}
		return code;
	}

}
