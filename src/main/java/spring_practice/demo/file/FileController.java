package spring_practice.demo.file;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String upLoadFile(
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
    public void downloadFile(@PathVariable("fileName") String fileName,
                             HttpServletResponse response) throws IOException {
        fileService.download(fileName, response);
    }



//    @GetMapping("/attach/{id}")
//    public ResponseEntity<Resource> downloadAttach(@PathVariable int id) throws MalformedURLException {
//        AbstractDocument.Content content = contentService.getContent(id);
//
//        String storeFilename = content.getAttachFile().getStoreFilename();
//        String uploadFilename = content.getAttachFile().getUploadFilename();
//
//        UrlResource urlResource = new UrlResource("file:" + fileStore.getFullPath(storeFilename));
//
//        // 업로드 한 파일명이 한글인 경우 아래 작업을 안해주면 한글이 깨질 수 있음
//        String encodedUploadFileName = UriUtils.encode(uploadFilename, StandardCharsets.UTF_8);
//        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
//
//        // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .body(urlResource);
//    }



}
