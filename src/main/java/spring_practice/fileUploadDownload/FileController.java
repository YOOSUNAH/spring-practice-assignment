package spring_practice.fileUploadDownload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String upLoadFile(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("## uploadFile 시작 ");
        if (file.isEmpty()) {
            log.error("파일이 비어있습니다.");
        }

        fileService.uploadFile(name, file);
        log.info("## uploadFile 완료 ");

        return "fileResult";
    }

    @PostMapping("/upload/multiFile")
    public String uploadMultiFile(
            @RequestParam ArrayList<MultipartFile> files,
            Model model
    ) throws IOException {

        log.info("## uploadMultiFile 시작 ");
        fileService.uploadMultiFile(files, model);
        log.info("## uploadMultiFile 완료 ");

        return "fileResult";
    }

    @GetMapping("/fileResult")
    public String getFileList(Model model ){
        List<String> fileNames = fileService.getAllFiles();

        log.info("## getFileList 시작 ");
        model.addAttribute("files", fileNames);
        log.info("## getFileList 완료 ");

        return "fileResult";
    }

    @GetMapping("/download/{fileName}")
    public Resource downloadFile(@PathVariable String fileName)throws MalformedURLException {
        return fileService.download(fileName);
    }

}
