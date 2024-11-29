package spring_practice.fileUploadDownload;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    // 파일 저장 경로
    @Value("${file.upload.path}")
    private String uploadPath;

    @Transactional
    public void uploadFile(String name, MultipartFile file) throws IOException {

        // 2. 원본 파일 이름 알아오기
        String originalFileName = file.getOriginalFilename();

        // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
        UUID uuid = UUID.randomUUID();
        String savedFileName = uuid.toString() + "_" + originalFileName;

        // 4. 파일 생성
        File newFile = new File(uploadPath + savedFileName);

        // 5. 서버로 전송
        file.transferTo(newFile);

        long size = file.getSize();
        log.info("size : " + size);
        log.info("originalFileName : " + originalFileName);
        log.info("name : " + name);
    }

    @Transactional
    public void uploadMultiFile(ArrayList<MultipartFile> multiFile, Model model ) throws IOException {
        ArrayList<String> originalFileNameList = new ArrayList<String>();

        for (MultipartFile file : multiFile) {

            // 2. 원본 파일 이름 알아오기
            String originalFileName = file.getOriginalFilename();

            // 파일 이름을 리스트에 추가
            originalFileNameList.add(originalFileName);

            // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
            // UUID 클래스 사용
            UUID uuid = UUID.randomUUID();
            String savedFileName = uuid.toString() + "_" + originalFileName;

            // 4. 파일 생성
            File newFile = new File(uploadPath + savedFileName);

            // 5. 서버로 전송
            file.transferTo(newFile);

            // Model 설정 : 뷰 페이지에서 원본 파일 이름 출력
            model.addAttribute("originalFileNameList", originalFileNameList);
        }
    }

    @Transactional
    public List<String> getAllFiles() {
        File dir = new File(uploadPath);
        String[] fileNames = dir.list();
        return Arrays.asList(fileNames);
    }

    @Transactional
    public Resource download(String fileName) {
        try {

            Path filePath = new File(uploadPath, fileName).toPath();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("file not found : " + fileName);
            }
        } catch (MalformedURLException e){
            throw new RuntimeException("file not found : " + fileName);
        }

    }


}
