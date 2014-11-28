package jp.ac.shinshu_u;


public class SetCodelist{

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

		@Override
		public String toString() {
			return name;
		}
	}

}
