package se.haxtrams.launchpad.backend.controller.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.haxtrams.launchpad.backend.converter.ApiConverter;
import se.haxtrams.launchpad.backend.exceptions.domain.NotFoundException;
import se.haxtrams.launchpad.backend.model.api.response.VideoFileResponse;
import se.haxtrams.launchpad.backend.service.SystemService;
import se.haxtrams.launchpad.backend.service.VideoService;

import java.util.Optional;

import static se.haxtrams.launchpad.backend.helper.ResponseHelper.createSimpleResponse;

@RestController
@RequestMapping("/api/v1/launcher")
public class LauncherController {
    private final VideoService videoService;
    private final SystemService systemService;
    private final ApiConverter apiConverter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public LauncherController(VideoService videoService, SystemService systemService, ApiConverter apiConverter) {
        this.videoService = videoService;
        this.systemService = systemService;
        this.apiConverter = apiConverter;
    }

    @PostMapping("/video/{id}")
    public ResponseEntity<VideoFileResponse> launchVideo(@PathVariable("id") Long id) {
        var video = videoService.findVideoById(id);
        systemService.openVideo(video);

        return ResponseEntity.ok(apiConverter.toVideoFileResponse(video));
    }

    @PostMapping("/video/shuffle")
    public ResponseEntity<VideoFileResponse> shuffleVideo(@RequestParam(value = "filter", required = false) Optional<String> filter) {
        var video = videoService.findRandomVideo(filter.orElse(""));
        systemService.openVideo(video);

        return ResponseEntity.ok(apiConverter.toVideoFileResponse(video));
    }

    @PostMapping("/video/kill")
    public ResponseEntity<String> killVideoProcess() {
        systemService.killVideoProcess();

        return createSimpleResponse(HttpStatus.OK);
    }

    @PostMapping("/video/{id}/location")
    public ResponseEntity<VideoFileResponse> launchVideoLocation(@PathVariable("id") Long id) {
        var video = videoService.findVideoById(id);
        systemService.openFileLocation(video);

        return ResponseEntity.ok(apiConverter.toVideoFileResponse(video));
    }

    @ExceptionHandler
    private ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        log.warn("File not found request in Launcher controller", e);
        return createSimpleResponse(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException(Exception e) {
        log.error("Unexpected exception in Launcher controller", e);
        return createSimpleResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
