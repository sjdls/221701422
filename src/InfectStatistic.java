import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * InfectStatistic
 * TODO
 *
 * @author xxx
 * @version xxx
 * @since xxx
 * 注意排序及其他要求的功能
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
	private static String[] commandStrings = { "-log", "-out", "-date", "-type", "-province" };

	// 可选择[ip： infection patients 感染患者，sp： suspected patients 疑似患者，cure：治愈 ，dead：死亡患者]，使用缩写选择
	private static String[] typeAbbreviationCommandStrings = { "ip", "sp", "cure", "dead" };
	// 存放患者的类型
	private static String[] typeCharCommondStrings = { "感染患者", "疑似患者", "治愈", "死亡" };

	private static String[] cureAndDeadStrings = { "治愈", "死亡"};
	private static String[] addAndExcludeAndDiagnosisStrings = { "新增", "排除", "确诊感染" };
	private static String inflowsString = "流入";

	// 存放输入信息
	private static HashMap<String, String> inputHashMap = new HashMap<String, String>();
	// 存放省份与患者
	private static HashMap<String, HashMap<String, Long>> provinceHashMap = new HashMap<String, HashMap<String, Long>>();

	private static String logNameString = "";
	private static String outNameString = "";
	private static String dateString = "";
	private static String[] typeStrings;
	private static String[] provinceStrings;

	private static String[] logNameStrings;

	public static void main(String[] args) {
		init(args);
		try {
			readLogName();
			readLogContent();
			// System.out.println(dateString);
			System.out.println(provinceHashMap.get("全国").get("死亡") + "");
			// System.out.println(patientsHashMap.get("治愈") + "");
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	private static void init(String[] args) {
		for (String string : commandStrings) {
			inputHashMap.put(string, "");
		}
		String i = "";
		for (String string : args) {
			// System.out.println(i+":"+string.charAt(0));
			if (!string.equals("list")) {
				if (string.charAt(0) == '-') {
					i = string;
				} else {
					inputHashMap.put(i, inputHashMap.get(i) + " " + string);
				}
			}
		}
		String[] temStrings;
		temStrings = inputHashMap.get(commandStrings[0]).split(" ");
		logNameString = temStrings[1];
		temStrings = inputHashMap.get(commandStrings[1]).split(" ");
		outNameString = temStrings[1];
		temStrings = inputHashMap.get(commandStrings[2]).split(" ");
		if (temStrings.length > 1) {
			dateString = temStrings[1];
		}
		temStrings = inputHashMap.get(commandStrings[3]).split(" ");
		typeStrings = new String[temStrings.length - 1];
		System.arraycopy(temStrings, 1, typeStrings, 0, typeStrings.length);
		temStrings = inputHashMap.get(commandStrings[4]).split(" ");
		provinceStrings = new String[temStrings.length - 1];
		System.arraycopy(temStrings, 1, provinceStrings, 0, provinceStrings.length);

		HashMap<String, Long> patientsHashMap = new HashMap<String, Long>();
		Long temLong = new Long(0);
		for (String string : typeCharCommondStrings) {
			patientsHashMap.put(string, temLong);
		}
		provinceHashMap.put("全国", patientsHashMap);
	}

	private static void readLogName() throws ParseException {
		File file = new File(logNameString);
		String[] fileList = file.list();
		String temString = "";
		if (dateString.length() != 0) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date inputDate = simpleDateFormat.parse(dateString);
			for (int i = 0; i < fileList.length; i++) {
				Date date = simpleDateFormat.parse(fileList[i].split("\\.")[0]);
				if (inputDate.compareTo(date) > -1) {
					temString += " " + fileList[i];
					// System.out.println(simpleDateFormat.format(date));
				}
			}
		} else {
			for (int i = 0; i < fileList.length; i++) {
				temString += " " + fileList[i];
			}
		}
		String[] temStrings = temString.split(" ");
		logNameStrings = new String[temStrings.length - 1];
		System.arraycopy(temStrings, 1, logNameStrings, 0, logNameStrings.length);
	}

	private static void readLogContent() throws IOException {
		Charset.defaultCharset();
		for (String string : logNameStrings) {
			String pathString = logNameString + string;
			// String fileCharsetString = getFileCharset(pathString);
			// System.out.println("编码格式为" + fileCharsetString);
			File file = new File(pathString);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "UTF-8"));

			String lineString;
			while ((lineString = bufferedReader.readLine()) != null) {
				lineString = lineString.trim();
				if (!lineString.startsWith("//")) {
					dealLogContent(lineString);
					System.out.println(lineString);
				}
			}
		}
	}

	/**
	 * private static String getFileCharset(String pathNameString) throws IOException {
		InputStream inputStream = new FileInputStream(pathNameString);
		byte[] head = new byte[3];
		inputStream.read(head);
	
		String charset = "GBK";// 或GB2312，即ANSI
		if (head[0] == -1 && head[1] == -2) {// 0xFFFE
			charset = "UTF-16";
		} else if (head[0] == -2 && head[1] == -1) {// 0xFEFF
			charset = "Unicode";// 包含两种编码格式：UCS2-Big-Endian和UCS2-Little-Endian
		} else if (head[0] == -27 && head[1] == -101 && head[2] == -98) {
			charset = "UTF-8"; // UTF-8(不含BOM)
		} else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
			charset = "UTF-8"; // UTF-8-BOM
		}
	
		inputStream.close();
		// System.out.println(code);
	
		return charset;
	}
	 */

	private static void dealLogContent(String lineString) {
		String[] inputStrings = lineString.split(" ");
		if (!provinceHashMap.containsKey(inputStrings[0])) {
			// System.out.println("不包含");
			HashMap<String, Long> patientsHashMap = new HashMap<String, Long>();
			Long temLong1 = new Long(0);
			for (String string : typeCharCommondStrings) {
				patientsHashMap.put(string, temLong1);
			}
			provinceHashMap.put(inputStrings[0], patientsHashMap);
		}
		if (inputStrings.length == 3) {
			if(Arrays.asList(cureAndDeadStrings).contains(inputStrings[1])) {
				cureAndDead(inputStrings);
				inputStrings[0] = "全国";
				cureAndDead(inputStrings);
			}
		}
	}

	private static void cureAndDead(String[] inputStrings) {
		Long originalLong = new Long(0);
		Long changesLong = new Long(0);
		originalLong = provinceHashMap.get(inputStrings[0]).get(typeCharCommondStrings[0]);
		changesLong = Long.valueOf(inputStrings[2].substring(0, inputStrings[2].length() - 1));
		originalLong -= changesLong;
		provinceHashMap.get(inputStrings[0]).put(typeCharCommondStrings[0], originalLong);
		originalLong = provinceHashMap.get(inputStrings[0]).get(inputStrings[1]);
		originalLong += changesLong;
		provinceHashMap.get(inputStrings[0]).put(inputStrings[1], originalLong);
	}
}
