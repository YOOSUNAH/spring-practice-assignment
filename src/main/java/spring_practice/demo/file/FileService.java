package spring_practice.demo.file;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    // 1. 파일 저장 경로
    @Value("${file.upload.path}")
    private String uploadPath;

    @Transactional
    public void uploadFileWithBody(byte[] fileData, String fileName) throws IOException {

        // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
        UUID uuid = UUID.randomUUID();

        String savedFileName = String.format("%s_%s", uuid.toString(), fileName); // 예: originalFileName = 스크린캡쳐.png

        // 4. 전체 파일 경로 생성
        Path filePath = Paths.get(uploadPath, savedFileName);

        // 5. 디렉토리 존재 확인 및 생성
        Files.createDirectories(filePath.getParent());

        // 6. 파일 쓰기 // transferTo() 대신 Files.write() 활용
        Files.write(filePath, fileData);
    }


    @Transactional
    public void uploadFileWithModel(MultipartFile file, String description) throws IOException {

        // 2. 원본 파일 이름 알아오기
        String originalFileName = file.getOriginalFilename();

        // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
        UUID uuid = UUID.randomUUID();

        String savedFileName = String.format("%s_%s_%s", description, uuid.toString(), originalFileName); // 예: originalFileName = 스크린캡쳐.png

        // 4. 파일 생성
        File newFile = new File(uploadPath + savedFileName);

        // 5. 서버로 전송
        file.transferTo(newFile);
    }

    @Transactional
    public void uploadFileWithRequestPart(MultipartFile file, RequestPartDto requestPartDto) throws IOException {

        // 2. 원본 파일 이름 알아오기
        String originalFileName = file.getOriginalFilename();

        // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
        UUID uuid = UUID.randomUUID();

        String name = requestPartDto.getFileName();
        String savedFileName = String.format("%s_%s_%s", name, uuid.toString(), originalFileName); // 예: originalFileName = 스크린캡쳐.png

        // 4. 파일 생성
        File newFile = new File(uploadPath + savedFileName);

        // 5. 서버로 전송
        file.transferTo(newFile);
    }


    @Transactional
    public void uploadFile(String name, MultipartFile file) throws IOException {

        // 2. 원본 파일 이름 알아오기
        String originalFileName = file.getOriginalFilename();

        // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
        UUID uuid = UUID.randomUUID();

        String savedFileName = String.format("%s_%s_%s", name, uuid.toString(), originalFileName); // 예: originalFileName = 스크린캡쳐.png

        // 4. 파일 생성
        File newFile = new File(uploadPath + savedFileName);

        // 5. 서버로 전송
        file.transferTo(newFile);
    }

    @Transactional
    public void uploadMultiFile(List<String> names, List<MultipartFile> multiFile, Model model) throws IOException {
        ArrayList<String> originalFileNameList = new ArrayList<String>();

        for (int i = 0; i < multiFile.size(); i++) {
            MultipartFile file = multiFile.get(i);

            // 2. 원본 파일 이름 알아오기
            String originalFileName = file.getOriginalFilename();

            // 파일 이름을 리스트에 추가
            originalFileNameList.add(originalFileName);

            // 3. 파일 이름이 중복되지 않도록 파일 이름 변경 : 서버에 저장할 이름
            // UUID 클래스 사용
            UUID uuid = UUID.randomUUID();

            // List<String> name -> 수동으로 입력한 이름들.
            String savedFileName = String.format("%s_%s_%s", names.get(i), uuid.toString(), originalFileName);  // 예: originalFileName = 스크린캡쳐.png

            // 4. 파일 생성
            File newFile = new File(uploadPath + savedFileName);

            // 5. 서버로 전송
            file.transferTo(newFile);

            // Model 설정 : 뷰 페이지에서 원본 파일 이름 출력
            model.addAttribute("originalFileNameList", originalFileNameList);
        }
    }

    @Transactional
    public List<String> getFileList() {
        File dir = new File(uploadPath);
        String[] fileNames = dir.list();
        return Arrays.asList(fileNames);
    }


    @Transactional
    public void download(String fileName,
                         HttpServletResponse response) throws IOException {

        Path filePath = Paths.get(uploadPath).resolve(fileName);
        // 기본 업로드 경로를 Path 객체로 만든다,
        // resolve(fileName): 경로에 파일 이름을 추가해 파일의 전체 경로를 생성한다.
        // 예: uploadPath가 /uploads이고 fileName이 example.txt라면 결과는 /uploads/example.txt

        File file = filePath.toFile();
        // Path 객체를 Java File 객체로 변환

        if (!file.exists()) {
            log.error("파일을 찾을 수 없다: {}", fileName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일을 찾을 수 없습니다.");
        }

        // url 인코딩 하기
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        // 파일 이름을 URL-safe 형식으로 인코딩. 파일 이름에 공백, 특수 문자 등이 포함되어 있더라도 안전하게 전송할 수 있다.

        // 전송 data의 인코딩 방식 설정
//        response.setHeader("Content-Transfer-Encoding", "binary");
//        -> 이걸로 대체가 가능한가??


        // == 응답 헤더 설정 ==
        // ContentType 설정
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE); // 바이너리 데이터임을 명시
        String DISPOSITION = "attachment; filename=\"" + encodedFileName + "\"";
        // Header 설정
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, DISPOSITION);
        // Content-Disposition에 attachment를 주는 경우, Body에 오는 값을 다운로드 받으라는 의미, fileName은 다운로드 될 파일명

        // ContentLength 설정
        response.setContentLengthLong(file.length()); //  파일 크기를 응답에 포함

        // 파일 스트림 전송
         /*
            BufferedInputStream
            java.io의 가장 기본 파일 입출력 클래스
            입력 스트림(통로)을 생성해줌
            사용법은 간단하지만, 버퍼를 사용하지 않기 때문에 느림
            속도 문제를 해결하기 위해 버퍼를 사용하는 다른 클래스와 같이 쓰는 경우가 많음

            BufferedOutputStream
            java.io의 가장 기본이 되는 파일 입출력 클래스
            출력 스트림(통로)을 생성해줌
            사용법은 간단하지만, 버퍼를 사용하지 않기 때문에 느림
            속도 문제를 해결하기 위해 버퍼를 사용하는 다른 클래스와 같이 쓰는 경우가 많음
            */
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = new byte[8192]; // 8KB 버퍼  // 읽어올 파일을 일정한 크기(8kb)로 잘라 담기 위한 바이트 버퍼.
            int bytesRead;

             /*
            모두 현재 파일 포인터 위치를 기준으로 함 (파일 포인터 앞의 내용은 없는 것처럼 작동)
            int read() : 1byte씩 내용을 읽어 정수로 반환
            int read(byte[] b) : 파일 내용을 한번에 모두 읽어서 배열에 저장
            int read(byte[] b. int off, int len) : 'len'길이만큼만 읽어서 배열의 'off'번째 위치부터 저장
            */
            // 읽은 데이터가 없을 때까지 반복
            while ((bytesRead = in.read(buffer)) != -1) {  // in.read(buffer) 파일 데이터 읽기
                out.write(buffer, 0, bytesRead); //  읽은 데이터를 응답
//                log.info("파일 다운로드 완료: {}", fileName);
            }

        } catch (IOException e) {
            log.error("파일 다운로드 중 오류 발생: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "다운로드중 오류 발생");
        }

//        response.getOutputStream().flush(); // 버퍼 비우기  // flush()는 출력 스트림에 남아 있는 데이터를 강제로 비워(전송 완료) 클라이언트로 보냅니다.
//        response.getOutputStream().close(); // response 닫기 // in.close()와 out.close() : 스트림을 닫아 메모리 누수를 방지하고, 리소스를 해제합니다.
        // flush, close  -> try catch 문으로 잡을 경우 안써도 된다.
    }



}
