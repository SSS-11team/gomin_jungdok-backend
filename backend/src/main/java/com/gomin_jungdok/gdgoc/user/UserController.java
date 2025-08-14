package com.gomin_jungdok.gdgoc.user;

import com.gomin_jungdok.gdgoc.auth.AppleAndGoogleService;
import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.post.PostService;
import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.user.Dto.ProfileImageRequestDto;
import com.gomin_jungdok.gdgoc.user.Dto.UpdateNicknameDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private AppleAndGoogleService appleAndGoogleService;


    @GetMapping("/user/")
    @Operation(summary = "api/auth/user/",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 정보 리턴",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class)))
    })
    public ResponseEntity<Map<String, Object>> getUser(
            @Parameter(description = "jwtaccessToken", required = true)
            @RequestHeader("Authorization") String jwtaccessToken) throws Exception {

        System.out.println("jwtaccessToken = " + jwtaccessToken);
        UserInfoDto userInfo = appleAndGoogleService.getUserInfoByJwt(jwtaccessToken);
        System.out.println("userInfo = " + userInfo);


        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "유저 정보 리턴");
        response.put("userInfo", userInfo);


        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwtaccessToken) // 헤더에 액세스 토큰 추가
                .body(response); // 바디에 유저 정보 포함
    }


    // @PutMapping("/{userid}/updateNickName")
    @PutMapping("/user/updateNickName")
    @Operation(summary = "/api/auth/user/updateNickName"
            // ,security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "닉네임 변경 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 201, \"message\": \"닉네임 변경 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "닉네임을 입력해주세요",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 400, \"message\": \"닉네임을 입력해주세요\"}"))),
            @ApiResponse(responseCode = "500", description = "닉네임 변경시 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 500, \"message\": \"닉네임 변경시 오류\"}")))
    })
    public ResponseEntity<Map<String, Object>> updateNickname(
            @RequestHeader("Authorization") String bearerToken,
            // @PathVariable Long userid,

            @Parameter(description = "새로운 닉네임")
            @RequestBody UpdateNicknameDto newNickname) throws Exception {

        String jwtToken = bearerToken.replace("Bearer ", "");

        if(newNickname.getNickname() == null || newNickname.getNickname().equals("")) {
            return ResponseEntity.badRequest().body(
                    Map.of("statusCode", 400, "message", "닉네임을 입력해주세요")
            );
        }

        userService.updateNickname(jwtToken, newNickname.getNickname());
        //userService.updateNickname(userid, newNickname.getNickname());

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "닉네임 변경 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping(value = "/user/updateProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PutMapping(value = "/{userid}/updateProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "/api/auth/user/updateProfileImage",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프로필 사진 변경 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 201, \"message\": \"프로필 사진 변경 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "프로필 사진을 선택해주세요",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 400, \"message\": \"프로필 사진을 선택해주세요\"}"))),
            @ApiResponse(responseCode = "500", description = "프로필 사진 변경시 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 500, \"message\": \"프로필 사진 변경시 오류\"}")))
    })
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            @RequestHeader("Authorization") String bearerToken,
            // @PathVariable Long userid,

            @Parameter(description = "새로운 프로필 사진")
            @ModelAttribute ProfileImageRequestDto newImage) throws Exception {

        String jwtToken = bearerToken.replace("Bearer ", "");

        if(newImage.getImage() == null || newImage.getImage().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("statusCode", 400, "message", "프로필 사진을 선택해주세요")
            );
        }

        userService.updateProfile(jwtToken, newImage.getImage());
        // userService.updateProfile(userid, newImage.getImage());

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "프로필 사진 변경 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    //내가 쓴 고민글 리스트 불러오기 api
    @GetMapping("/{id}/post")
    @Operation(summary = "/api/auth/{id}/post")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PostListResponseDto.class)))
    public ResponseEntity<Map<String, Object>> getPost(

            @Parameter(description = "user의 id번호")
            @PathVariable Long id,

            @Parameter(description = "가져올 게시글 개수 (기본값: 10)")
            @RequestParam(name = "size", defaultValue = "10") int size,

            @Parameter(description = "마지막 게시글 ID (페이징 처리용)")
            @RequestParam(name = "last-id", required = false) Long lastId,

            @Parameter(description = "고민글 카테고리 필터링")
            @RequestParam(name = "category", required = false) List<String> category

    ) {

        PostListResponseDto responseDto = postService.getUserPosts(id, size, lastId, category);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "고민글 리스트 반환 성공");
        response.put("data", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

