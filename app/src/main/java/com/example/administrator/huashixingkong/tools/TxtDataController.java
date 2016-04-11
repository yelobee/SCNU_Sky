package com.example.administrator.huashixingkong.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class TxtDataController {

	public static String DATA = null;
	private String filePath = null;

	public TxtDataController(String path) {
		// 在此添加文件路径
		this.filePath = Environment.getExternalStorageDirectory().getPath()
				+ path;
	}

	public String getData() throws IOException {
		// 从路径进行json文件读写
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		String temp = null;
		StringBuffer sb = new StringBuffer();
		temp = br.readLine();
		while (temp != null) {
			sb.append(temp + "");
			temp = br.readLine();
		}
		DATA = sb.toString();
		br.close();
		return DATA;
	}
	
	public void writeData(String data) throws IOException{
		File file =new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter bw=new BufferedWriter(new FileWriter(file));
		bw.write(data);
		bw.close();
	}

}
