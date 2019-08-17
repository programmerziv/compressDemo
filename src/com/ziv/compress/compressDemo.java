package com.ziv.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
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
		//zipFileChannel();
		zipFileMap();
		
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
	 * NIO的channel加持，在内存中直接开辟了一个缓冲区，不必走内核态
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
	/**
	 * NIO的内存映射文件Map,直接是用户态和内核态的地址映射到一块直接物理内存上，速度贼快
	 * 但是用直接内存有缺点啊，1.不安全2.消耗多（不是在JVM里开辟，不知道什么时候垃圾回收）3.程序无法控制（由操作系统控制住了）
	 * 很快，和channel差不了多少
	 */
	public static void zipFileMap(){
		File zipFile = new File(ZIP_File);
		long beginTime = System.currentTimeMillis();
		try(ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
				WritableByteChannel writableByteChannel = Channels.newChannel(zipOut)){
			for (int i = 0; i < 10; i++) {
				//BufferedInputStream里封装了一个byte数组用于存储数据默认一次读8192个 8M 
				try(FileChannel  fileChannel = new  FileInputStream(JPG_File).getChannel()){
					zipOut.putNextEntry(new ZipEntry("无所谓"+i+".jpg"));
					// 此时用到了映射的关系
					MappedByteBuffer mappedByteBuffer = new RandomAccessFile(JPG_File,"r")
							.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
					writableByteChannel.write(mappedByteBuffer);
				}
			}
			// 打印耗时
			printInfo(beginTime);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 还有一种使用pipe的方法，是异步的，有点复杂没看懂，这里就先不写了
	 */
}
