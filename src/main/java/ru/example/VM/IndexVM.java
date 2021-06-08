package ru.example.VM;

import com.aspose.words.SaveFormat;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import ru.example.conversion.ConversionResultContainer;
import ru.example.conversion.ConversionService;

import java.io.InputStream;

import static org.zkoss.zul.Filedownload.save;

@VariableResolver(DelegatingVariableResolver.class)
public class IndexVM {
	@Getter	@Setter
	private String message = "ожидание загрузки...";
	@Getter @Setter
	private InputStream is;
	@Getter @Setter
	private String s;
	@Getter @Setter
	private Media media;
	@WireVariable("docConversionService")
	private ConversionService conversionService;
	@Getter @Setter
	private String saveFormat = "-1";
	@Getter @Setter
	private boolean hideConvertButton = true;
	@Getter @Setter
	private boolean hideRadioDocument = true;
	@Getter @Setter
	private boolean hideRadioSpreadsheet = true;



	@Command()
	@NotifyChange({"hideConvertButton", "hideRadioDocument", "hideRadioSpreadsheet", "message"})
	public void uploadFile(@ContextParam(ContextType.TRIGGER_EVENT) UploadEvent event) {
		media = event.getMedia();
		String uploadedFileName = media.getName();
		if (!conversionService.isFormatSupported(uploadedFileName)) {
			message = String.format("Формает файла {%s} не поддерживается, загрузите другой файл", uploadedFileName);
			return;
		}
		message = String.format("Загружен файл {%s}", uploadedFileName);
		hideConvertButton = false;
		if (media.isBinary()) {
			is = media.getStreamData();
		} else {
			s = media.getStringData();
		}
		if (conversionService.isSpreadsheet(uploadedFileName)) {
			hideRadioSpreadsheet = false;
			hideRadioDocument = true;
		} else {
			hideRadioSpreadsheet = true;
			hideRadioDocument = false;
		}

	}

	@Command()
	@NotifyChange({"message", "hideConvertButton", "saveFormat"})
	public void convert() {
		if (saveFormat.equalsIgnoreCase("-1")) {
			message = "Выберете формат для конвертации";
			return;
		}
		hideConvertButton = true;
		if (is == null) {
			message = "Сначала загрузите файл";
			return;
		}
		String uploadedFileName = media.getName();
		ConversionResultContainer result = conversionService.convert(is, Integer.parseInt(saveFormat), uploadedFileName);
		if (result != null) {
			message = "Конвертация прошла успешно, начинается скачивание...";
			saveFormat = "-1";
			save(result.getResultStream(), result.getContentType(), result.getFileName());
		}
	}

}
