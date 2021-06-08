package ru.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.example.conversion.ConversionResultContainer;
import ru.example.conversion.ConversionService;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class ConversionRestController {

	@Autowired
	private ConversionService conversionService;

	@PostMapping(value = "/convert", consumes = "multipart/form-data")
	public ResponseEntity<Resource> convert(@RequestParam(value = "file") MultipartFile file, @RequestParam("format") String format) throws IOException {
		ConversionResultContainer resultContainer = conversionService.convert(file.getInputStream(), format, file.getOriginalFilename());

		ByteArrayResource resource = new ByteArrayResource(StreamUtils.copyToByteArray(resultContainer.getResultStream()));

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resultContainer.getFileName());
		header.add("Accept-Ranges", "bytes");

		return ResponseEntity.ok()
				.headers(header)
				.contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType(resultContainer.getContentType()))
				.body(resource);
	}

	@GetMapping("/name")
	public ResponseEntity<String> convertServiceName() {
		return ResponseEntity
				.ok()
				.body("OODocumentConverter");
	}

	@GetMapping("/formats/isvalid")
	public ResponseEntity<Boolean> isValidFormatsByExtension(@RequestParam(name = "inFormat") String inExtension,
															 @RequestParam(name = "targetFormat") String targetExtension) {
		return ResponseEntity
				.ok()
				.body(conversionService.isValidFormat(inExtension, targetExtension));
	}
}
