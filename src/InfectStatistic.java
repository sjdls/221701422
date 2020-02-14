/**
 * InfectStatistic
 * TODO
 *
 * @author xxx
 * @version xxx
 * @since xxx
 */
class InfectStatistic {
	/**
	 * list的命令行参数的集合，类型为String[]
	 * -log 指定日志目录的位置，该项必会附带，请直接使用传入的路径，而不是自己设置路径
	 * -out 指定输出文件路径和文件名，该项必会附带，请直接使用传入的路径，而不是自己设置路径
	 * -date 指定日期，不设置则默认为所提供日志最新的一天。你需要确保你处理了指定日期以及之前的所有log文件
	 * -type 使用缩写选择如 -type ip 表示只列出感染患者的情况，-type sp cure则会按顺序[sp, cure]列出疑似患者和治愈患者的情况，不指定该项默认会列出所有情况。
	 * -province 指定列出的省，如-province 福建，则只列出福建，-province 全国 浙江则只会列出全国、浙江
	 */
	static String[] commandStrings = { "-log", "-out", "-date", "-type", "-province" };
	
	// 可选择[ip： infection patients 感染患者，sp： suspected patients 疑似患者，cure：治愈 ，dead：死亡患者]，使用缩写选择
	static String[] provinceCommandStrings = { "ip", "sp", "cure", "dead" };
	
	private static String[] inputStrings= {"","","","",""};

	public static void main(String[] args) {
		init(args);
		for(String string:inputStrings) {
			if(string.length()!=0) {
				System.out.println(string);
			}
		}
	}
	
	private static void init(String[] args) {
		int i=0;
		for (String string : args) {
			//System.out.println(i+":"+string.charAt(0));
			if(!string.equals("list")) {
				if(string.charAt(0)=='-') {
					for(int j=0;j<commandStrings.length;j++) {
						if(commandStrings[j].equals(string)) {
							i=j;
						}
					}
				}
				else {
					inputStrings[i]+=" "+string;
				}
			}
		}
	}
}
