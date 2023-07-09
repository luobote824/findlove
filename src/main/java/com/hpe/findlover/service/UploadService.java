package com.hpe.findlover.service;

import com.github.tobato.fastdfs.domain.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
	/**
	 * 上传文件并返回生成的文件路径
	 *
	 * @param file
	 * @return
	 */
	String uploadFile(MultipartFile file) throws IOException;

	String uploadFile(String content, String fileExtension);

	boolean deleteFile(String filePath);

	/**
	 * 下载文件
	 * @param filePath
	 * @return
	 */
	byte[] downloadFile(String filePath);

	/**
	 * 获取文件属性
	 * @param filePath
	 * @return
	 */
	FileInfo getFileInfo(String filePath);
}
