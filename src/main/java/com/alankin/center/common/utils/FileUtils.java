/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.utils;

import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import com.alankin.center.common.ftp.IFileFtp;

/**
 * 文件工具类
 * <p>
 * 
 * @author lansongtao
 * @Date 2015年10月19日
 * @since 1.0
 */
public class FileUtils {

	/** 日志. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 关闭流.
	 * 
	 * @param output
	 */
	private static void close(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				LOGGER.debug(e.getMessage());
			}
		}
	}

	/**
	 * 关闭流.
	 * 
	 * @param output
	 */
	private static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.debug(e.getMessage());
			}
		}
	}

	/**
	 * 创建文件.
	 * 
	 * @param filePath
	 *            文件路径
	 * @param content
	 *            文件内容
	 * @return 是否创建成功
	 * @throws IOException
	 */
	public static boolean createFile(String filePath, String content) {
		FileOutputStream output = null;
		BufferedOutputStream outputStream = null;
		try {
			File forder = new File(filePath.substring(0, filePath.lastIndexOf("/")));
			if (!forder.exists()) {
				forder.mkdirs();
			}
			output = new FileOutputStream(new File(filePath));
			outputStream = new BufferedOutputStream(output);
			outputStream.write(content.getBytes(IFileFtp.FILE_CHARSET));
			outputStream.flush();
			return true;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
			close(outputStream);
			close(output);
		}
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件.
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件内容
	 */
	public static String readFile(String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), IFileFtp.FILE_CHARSET));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				stringBuilder.append(tempString).append("\n");
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} finally {
			close(reader);
		}
		return stringBuilder.toString();
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}
		// 如果是文件,则直接删除
		if (file.isFile()) {
			return file.delete();
		}
		// 如果是文件夹,则递归删除
		if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0, length = filelist.length; i < length; i++) {
				File delfile = new File(filePath + "/" + filelist[i]);
				if (!delfile.isDirectory()) {
					delfile.delete();
				} else if (delfile.isDirectory()) {
					deleteFile(filePath + "/" + filelist[i]);
				}
			}
			file.delete();
		}
		return false;
	}

	/**
	 * 将字节流保存为文件
	 * 
	 * @param buf
	 * @param filePath
	 * @param fileName
	 * @throws IOException 
	 */
	public static long saveFileByBytes(byte[] buf, String filePath, String fileName) throws IOException {
		long fileSize = 0l;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				new File(filePath + "/" + fileName).getParentFile().mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (file != null) {
			fileSize = file.length();
		}
		return fileSize;
	}

	/**
	 * 将字节流保存为文件
	 * 
	 * @param buf
	 * @param filePath
	 * @param fileName
	 * @throws IOException 
	 */
	public static void saveFileByBase64(String base64Str, String filePath, String fileName) throws IOException {
		saveFileByBytes(Base64Utils.decodeFromString(base64Str), filePath, fileName);
	}

	/**
	 * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片
	 * 
	 * @param imageFile
	 * @return
	 */
	public static boolean isImage(byte[] buf) {
		Image img = null;
		try {
			img = ImageIO.read(new ByteArrayInputStream(buf));
			if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			img = null;
		}
	}

	/**
	 * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片
	 * 
	 * @param imageFile
	 * @return
	 */
	public static boolean isImage(String fileName, byte[] buf) {
		if (!StringUtils.isEmpty(fileName)) {
			String reg = ".*\\.(JPEG|jpeg|JPG|jpg|GIF|gif|BMP|bmp|PNG|png)$";
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(fileName.toLowerCase());
			if (!matcher.find()) {
				return false;
			}
		}

		return isImage(buf);
	}

	/**
	 * 获取文件夹下的所有文件
	 * 
	 * @param strPath
	 * @return
	 */
	public static List<File> getFileList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		List<File> filelist = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					filelist.add(files[i]);
				} else {
					continue;
				}
			}

		}
		return filelist;
	}

	/**
	 * 修改properties配置文件中的某个值
	 * 
	 * @param f
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	static public boolean setProfile(File f, String key, String value) {
		try {
			boolean res = false;
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
				String outstr = "";
				String line = "";

				while ((line = br.readLine()) != null) {
					if (line == "") // 如果为空
					{
						outstr += "\n";
						continue;
					}
					if (line.startsWith("#")) // 如果为注释
					{
						outstr += line + "\n";
						continue;
					}
					if (line.trim().startsWith(key)) {
						String[] keyandvalue = line.split("=");

						outstr += keyandvalue[0].toString() + "=" + value.toString() + "\n";

						res = true;
						continue;
					}
					outstr += line + "\n";

				}
				if (res) {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
					bw.write(outstr);

					bw.close();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	// /**
	// * 修改properties配置文件中的某个值
	// *
	// * @param f
	// * @param key
	// * @param value
	// * @return
	// * @throws IOException
	// */
	// @SuppressWarnings("resource")
	// static public boolean setProfile(File f, String key, String value) {
	// str[0]="#子系统查询接口 (第一个字段为记录ID别名统一改为seqId,条件需要包含序列ID，并按序列ID倒序排序)";
	// str[1]="#SQL结果集中的字段名，需要与下面配置的接口字段一一对应";
	// str[2]="#对应的接口 body 字段名,需要与上面的顺序一一对应";
	// str[3]="#子系统节点ID字段名";
	// str[4]="#ID字段名(递增的ID序列字段名)";
	// str[5]="#接口类型";
	// str[6]="#已同步的最大记录ID";
	// try {
	// boolean res = false;
	// if (f.exists()) {
	// BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
	// String outstr = "";
	// String line = "";
	//
	// int index = 0;
	// while ((line = br.readLine()) != null) {
	// if (line == "") // 如果为空
	// {
	// outstr += "\n";
	// continue;
	// }
	// if (line.startsWith("#")) // 如果为注释
	// {
	// outstr += str[index++] + "\n";
	// continue;
	// }
	// if (line.trim().startsWith(key)) {
	//// String[] keyandvalue = line.split("=");
	////
	//// outstr += keyandvalue[0].toString() + "=" + value.toString() + "\n";
	//
	// outstr += line + "\n";
	// res = true;
	// continue;
	// }
	// outstr += line + "\n";
	//
	// }
	// if (res) {
	// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
	// bw.write(outstr);
	//
	// bw.close();
	// return true;
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return false;
	//
	// }

}
