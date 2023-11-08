package ext.library.util;


import com.luciad.imageio.webp.WebPWriteParam;
import ext.library.exception.ResultException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图像 工具类
 *
 * @author zlh
 * @since 2023/11/08
 */
@Slf4j
public class ImageUtils {
    static final String WEBP_TYPE = "image/webp";

    /**
     * 转 webp 有损
     */
    public static byte[] toWebpLossy(byte[] imageBytes) {
        try {
            // 原图片
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(inputStream);
            // 压缩后
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
            ImageWriter writer = ImageIO.getImageWritersByMIMEType(WEBP_TYPE).next();
            WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            // 设置有损压缩
            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            //设置 80% 的质量。设置范围 0-1
            writeParam.setCompressionQuality(0.8f);

            // Save the image
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(image, null, null), writeParam);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.info("图片压缩失败", e);
            throw new ResultException("图片压缩失败");
        }
    }

    /**
     * 转 webp 无损
     * 无损压缩
     */
    public static byte[] toWebpLossless(byte[] imageBytes) {
        try {
            // 原图片
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(inputStream);
            // 压缩后
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
            ImageWriter writer = ImageIO.getImageWritersByMIMEType(WEBP_TYPE).next();
            WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            // 设置无损
            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);

            // 保存图片
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(image, null, null), writeParam);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.info("图片压缩失败", e);
            throw new ResultException("图片压缩失败");
        }
    }


}
