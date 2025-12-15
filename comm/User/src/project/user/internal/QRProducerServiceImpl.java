package project.user.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import kernel.util.UUIDGenerator;
import project.Constants;
import project.blockchain.QRProducerService;

public class QRProducerServiceImpl implements QRProducerService {

	@Override
	public String generate(String content) {
		String image_name = "/qr/" + UUIDGenerator.getUUID() + ".png";
		String filepath = Constants.IMAGES_DIR + image_name;
		File file = new File(filepath);
		int width = 691;
		int height = 691;
		String format = "png";
		Map hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(bitMatrix, format, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image_name;
	}

}
