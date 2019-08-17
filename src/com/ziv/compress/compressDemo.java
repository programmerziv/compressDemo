package com.ziv.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class compressDemo {
	
	
	private final static String ZIP_File = "E:\\2345Downloads\\alibaba.zip";
	private final static String JPG_File = "E:\\2345Downloads\\process.jpg";


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//zipFileNoBuffer();
		//zipFileBuffer();
		zipFileChannel();
	}
	
	private static void printInfo(long beginTime){
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-beginTime);
	}
	/**
	 * 未加任何优化的纯输入输出流搞成压缩的样子
	 */
	public static void zipFileNoBuffer(){
		File zipFile = new File(ZIP_File);
		try(ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))){
			//开始时间
			long beginTime = System.currentTimeMillis();
			
			for (int i = 0; i < 10; i++) {
				//普通的inputStrem 读一个写一个
				try(InputStream input = new FileInputStream(JPG_File)){
					zipOut.putNextEntry(new ZipEntry("无所谓"+i+".jpg"));
					int temp = 0;
					while((temp = input.read()) != -1){
						zipOut.write(temp);
					}
				}
			}
			// 打印耗时
			printInfo(beginTime);			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * buffered加持
	 */
	public static void zipFileBuffer(){
		File zipFile = new File(ZIP_File);
		try(ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
				BufferedOutputStream bufferedOutputStream =new BufferedOutputStream(zipOut)){
			//开始时间
			long beginTime = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				//BufferedInputStream里封装了一个byte数组用于存储数据默认一次读8192个 8M 
				try(BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(JPG_File))){
					zipOut.putNextEntry(new ZipEntry("无所谓"+i+".jpg"));
					int temp = 0;
					while((temp = bufferedInputStream.read()) != -1){
						bufferedOutputStream.write(temp);
					}
				}
			}
			// 打印耗时
			printInfo(beginTime);			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * NIO的channel加持
	 */
	public static void zipFileChannel(){
		File zipFile = new File(ZIP_File);
		long beginTime = System.currentTimeMillis();
		try(ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
				WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)){
			for (int i = 0; i < 10; i++) {
				//BufferedInputStream里封装了一个byte数组用于存储数据默认一次读8192个 8M 
				try(FileChannel  fileChannel = new  FileInputStream(JPG_File).getChannel()){
					zipOut.putNextEntry(new ZipEntry("无所谓"+i+".jpg"));
					//参数：传输位置，传输大小，传输通道
					fileChannel.transferTo(0, 1024000, writableByteChannel);
				}
			}
			// 打印耗时
			printInfo(beginTime);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
