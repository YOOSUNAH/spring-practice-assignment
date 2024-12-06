package spring_practice.demo.file;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.net.URLDecoder;


@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload/octet-stream", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public String uploadFileWithBody(
            @RequestBody byte[] fileBytes,
            @RequestHeader("X-Filename") String fileName
    ) throws IOException {

        // 파일 이름을 URL-decoding하여 원래의 파일 이름으로 복원
        String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
        fileService.uploadFileWithBody(fileBytes, decodedFileName);

        return "redirect:/fileList";
    }

    @PostMapping(value = "/upload/modelattribute", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFileWithModel(@ModelAttribute FileUploadRequest requestDto)
            throws IOException {

        if (requestDto.getFile().isEmpty()) {
            log.error("파일이 비어있습니다.");
            return "redirect:/fileUploadModelattribute";
        }
        fileService.uploadFileWithModel(requestDto.getFile(), requestDto.getDescription());
        return "redirect:/fileList";
    }

    @PostMapping(value = "/upload/request-part", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFileWithRequestPart(
            @RequestPart("file") MultipartFile file,
            @RequestPart("postPerson") String postPerson,
            @RequestPart("fileName") String fileName
    ) throws IOException {

        RequestPartDto requestPartDto = new RequestPartDto();
        requestPartDto.setPostPerson(postPerson);
        requestPartDto.setFileName(fileName);

        log.info("메서드 진입전");

        if (file.isEmpty()) {
            log.error("파일이 비어있습니다.");
            return "redirect:/fileUpload";
        }

        fileService.uploadFileWithRequestPart(file, requestPartDto);
        return "redirect:/fileList";
    }
    // ====================================================  //

    @PostMapping("/upload")
    public String upLoadSingleFile(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            log.error("파일이 비어있습니다.");
            return "redirect:/fileUpload";
        }

        fileService.uploadFile(name, file);

        return "redirect:/fileList";
    }

    @PostMapping("/upload/multiFile")
    public String uploadMultiFile(
            @RequestParam("names") List<String> names,
            @RequestParam("files") List<MultipartFile> files,
            Model model
    ) throws IOException {

        if (files.isEmpty()) {
            log.error("파일이 비어있습니다.");
            return "redirect:/fileUpload";
        }

        fileService.uploadMultiFile(names, files, model);

        return "redirect:/fileList";
    }

    @GetMapping("/fileList")
    public String getFileList(Model model) {

        List<String> fileNameList = fileService.getFileList();
        model.addAttribute("fileNameList", fileNameList);

        return "fileResult";
    }

    @GetMapping("/download/{fileName}")
    public void downloadFile(
            @PathVariable("fileName") String fileName,
            HttpServletResponse response
    ) throws IOException {

        fileService.download(fileName, response);
    }
}
