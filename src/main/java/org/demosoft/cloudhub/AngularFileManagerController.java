package org.demosoft.cloudhub;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.HttpStatus;
import org.demosoft.cloudhub.config.FileSystemConfig;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Andrii_Korkoshko on 12/7/2016.
 */
@Controller
public class AngularFileManagerController {


    private static final Logger LOG = LoggerFactory.getLogger(AngularFileManagerController.class);

    private static final long serialVersionUID = -8453502699403909016L;

    @Autowired
    private FileSystemConfig fileSystemConfig;

    enum Mode {
        list, rename, copy, delete, savefile, editfile, createFolder, changePermissions, compress, extract
    }

    private String REPOSITORY_BASE_URL = "";
    private String REPOSITORY_BASE_PATH = "/tmp";
    // private String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"; // (2001-07-04 12:08:56)
    private String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z"; // (Wed, 4 Jul 2001 12:08:56)
    @Autowired
    ServletContext context;

    @PostConstruct
    void init() throws ServletException {
        // load from properties file REPOSITORY_BASE_PATH, REPOSITORY_BASE_URL and DATE_FORMAT, use default if missing
        // throw exception in case of bad data
        //InputStream propertiesFile = null;
        try {
            //String splittedThisServletPara[] = getInitParameter("properties").split(",");
            //propertiesFile = getServletContext().getResourceAsStream(splittedThisServletPara[1].trim());				// getDBProfile the default one
            // if (propertiesFile != null) {
            //  Properties prop = new Properties();
            // load a properties file from class path, inside static method
            //   prop.load(propertiesFile);
            REPOSITORY_BASE_URL = fileSystemConfig.getRepositoryBasePath();
               /* if (!"".equals(REPOSITORY_BASE_URL) && !new File(getServletContext().getRealPath(REPOSITORY_BASE_URL)).isDirectory()) {
                    // REPOSITORY_BASE_URL is not empty AND NOT a directory
                    throw new ServletException("invalid repository.base.url");
                }*/
            REPOSITORY_BASE_PATH = fileSystemConfig.getRepositoryBasePath();
            if (!"".equals(REPOSITORY_BASE_PATH) && !new File(REPOSITORY_BASE_PATH).isDirectory()) {
                // REPOSITORY_BASE_URL is not empty AND not a directory
                throw new ServletException("invalid repository.base.path");
            }

            //DATE_FORMAT = prop.getProperty("date.format", DATE_FORMAT);
            try {
                if (new SimpleDateFormat(DATE_FORMAT).format(new Date()) == null) {
                    // Invalid date format
                    LOG.debug("throw invalid date.format ");
                    throw new ServletException("invalid date.format");
                }
            } catch (NullPointerException | IllegalArgumentException e) {
                throw new ServletException("invalid date.format", e);
            }
            //  }

            //else {
            //     LOG.debug("NO properties File");
            //   }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @GetMapping("/handler")
    void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Catch download requests

        // [$config.downloadFileUrl]?mode=download&preview=true&path=/public_html/image.jpg
        String mode = request.getParameter("mode");
        boolean preview = BooleanUtils.toBoolean(request.getParameter("preview"));
        String path = request.getParameter("path");
        LOG.debug("doGet: {} mode: {} preview: {}", path, mode, preview);

        File file = new File(REPOSITORY_BASE_PATH, path);

        if (!file.isFile()) {
            // if not a file, it is a folder, show this error.
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
            return;
        }

        // se imageName non ha estensione o non è immagine sballa! ;)
        // response.setHeader("Content-Type", getServletContext().getMimeType(imageName));
        response.setHeader("Content-Type", context.getMimeType(file.getName()));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

        FileInputStream input = null;
        BufferedOutputStream output = null;
        try {

            input = new FileInputStream(file);
            output = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[8192];
            for (int length = 0; (length = input.read(buffer)) > 0; ) {
                output.write(buffer, 0, length);
            }
        } catch (Throwable t) {
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException logOrIgnore) {
                }
            }
        }
    }

    @PostMapping("/handler")
    void post(HttpServletRequest request, HttpServletResponse response, MultipartFile[] uploadingFiles) throws IOException {
        LOG.debug("doPost");
        try {
            // if request contains multipart-form-data
            if (ServletFileUpload.isMultipartContent(request)) {
                StandardMultipartHttpServletRequest mRequest = (StandardMultipartHttpServletRequest) request;
                LOG.debug("doPost");
                newUploadFile(mRequest, response);
            }
            // all other post request has jspn params in body
            else {
                fileOperation(request, response);
            }
        } catch (Throwable t) {
            setError(t, response);
        }
    }

    private void setError(Throwable t, HttpServletResponse response) throws IOException {
        try {
            // { "result": { "success": false, "error": "message" } }
            JSONObject responseJsonObject = error(t.getMessage());
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(responseJsonObject);
            out.flush();
        } catch (Throwable x) {
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, x.getMessage());
        }

    }

    private int maxFileSize = 50 * 1024;
    private int maxMemSize = 4 * 1024;

    private void newUploadFile(StandardMultipartHttpServletRequest request, HttpServletResponse response) throws ServletException {
        Map<String, MultipartFile> fileMap = request.getFileMap() != null ? request.getFileMap() : new HashMap<>();
        String destination = request.getParameter("destination");
        for (Map.Entry<String, MultipartFile> multipartFileEntry : fileMap.entrySet()) {
            try {
                InputStream file = multipartFileEntry.getValue().getInputStream();
                File f = new File(REPOSITORY_BASE_PATH + destination, multipartFileEntry.getValue().getOriginalFilename());
                if (!write(file, f)) {
                    LOG.debug("write error");
                    throw new Exception("write error");
                }
            } catch (IOException e) {
                LOG.debug("Cannot write file");
                throw new ServletException("Cannot write file", e);
            } catch (Exception e) {
                LOG.debug("Cannot write file");
                throw new ServletException("Cannot write file", e);
            }
        }
        try {
        JSONObject responseJsonObject = null;
        responseJsonObject = this.success(responseJsonObject);
        response.setContentType("application/json");
        PrintWriter out = null;
            out = response.getWriter();
        out.print(responseJsonObject);
        out.flush();
        } catch (IOException e) {
            LOG.debug("Cannot write file");
            throw new ServletException("Cannot write file", e);
        }
    }

    private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // URL: $config.uploadUrl, Method: POST, Content-Type: multipart/form-data
        // Unlimited file upload, each item will be enumerated as file-1, file-2, etc.
        // [$config.uploadUrl]?destination=/public_html/image.jpg&file-1={..}&file-2={...}
        LOG.debug("upload now");
        try {
            String destination = null;
            Map<String, InputStream> files = new HashMap<String, InputStream>();
///////////////
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            factory.setSizeThreshold(maxMemSize);
            // Location to save data that is larger than maxMemSize.
            // factory.setRepository(new File("c:\\temp"));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum file size to be uploaded.
            upload.setSizeMax(100 * 1024);

            // Parse the request to getDBProfile file items.
            List fileItems = upload.parseRequest(request);
            ////////////////

            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                    if ("destination".equals(item.getFieldName())) {
                        destination = item.getString();
                    }
                } else {
                    // Process form file field (input type="file").
                    files.put(item.getName(), item.getInputStream());
                }
            }
            if (files.size() == 0) {
                LOG.debug("file size  = 0");
                throw new Exception("file size  = 0");
            } else {
                for (Map.Entry<String, InputStream> fileEntry : files.entrySet()) {
                    File f = new File(REPOSITORY_BASE_PATH + destination, fileEntry.getKey());
                    if (!write(fileEntry.getValue(), f)) {
                        LOG.debug("write error");
                        throw new Exception("write error");
                    }
                }

                JSONObject responseJsonObject = null;
                responseJsonObject = this.success(responseJsonObject);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(responseJsonObject);
                out.flush();
            }
        } catch (FileUploadException e) {
            LOG.debug("Cannot parse multipart request: DiskFileItemFactory.parseRequest");
            throw new ServletException("Cannot parse multipart request: DiskFileItemFactory.parseRequest", e);
        } catch (IOException e) {
            LOG.debug("Cannot parse multipart request: item.getInputStream");
            throw new ServletException("Cannot parse multipart request: item.getInputStream", e);
        } catch (Exception e) {
            LOG.debug("Cannot write file");
            throw new ServletException("Cannot write file", e);
        }

    }

    private boolean write(InputStream inputStream, File f) {
        boolean ret = false;

        // http://www.mkyong.com/java/how-to-convert-inputstream-to-file-in-java/
        OutputStream outputStream = null;

        try {
            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(f);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            ret = true;

        } catch (IOException e) {
            LOG.error("", e);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOG.error("", e);
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    LOG.error("", e);
                }

            }
        }
        return ret;
    }

    private void fileOperation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject responseJsonObject = null;
        try {
            // legge il parametro json
            StringBuilder sb = new StringBuilder();
            BufferedReader br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            JSONObject jObj = new JSONObject(sb.toString());
            // legge mode e chiama il metodo aapropriato
            // JSONObject params = jObj.getJSONObject("params");
            JSONObject params = jObj;
            //AngularFileManagerController.Mode mode = AngularFileManagerController.Mode.valueOf(params.getString("mode"));
            AngularFileManagerController.Mode mode = AngularFileManagerController.Mode.valueOf(params.getString("action"));
            switch (mode) {
                case createFolder:
                    responseJsonObject = addFolder(params);
                    break;
                case changePermissions:
                    responseJsonObject = changePermissions(params);
                    break;
                case compress:
                    responseJsonObject = compress(params);
                    break;
                case copy:
                    responseJsonObject = copy(params);
                    break;
                case delete:
                    responseJsonObject = delete(params);
                    break;
                case editfile: // getDBProfile content
                    responseJsonObject = editFile(params);
                    break;
                case savefile: // save content
                    responseJsonObject = saveFile(params);
                    break;
                case extract:
                    responseJsonObject = extract(params);
                    break;
                case list:
                    responseJsonObject = list(params);
                    break;
                case rename:
                    responseJsonObject = rename(params);
                    break;
                default:
                    throw new ServletException("not implemented");
            }
            if (responseJsonObject == null) {
                responseJsonObject = error("generic error : responseJsonObject is null");
            }
        } catch (Exception e) {
            responseJsonObject = error(e.getMessage());
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseJsonObject);
        out.flush();
    }

    private JSONObject list(JSONObject params) throws ServletException {
        try {
            boolean onlyFolders = false;
            if (params.has("onlyFolders")) {
                onlyFolders = params.getBoolean("onlyFolders");
            }

            String path = params.getString("path");
            LOG.debug("list path: {} onlyFolders, try 中文: {}", path, onlyFolders);

            List<JSONObject> resultList = new ArrayList<JSONObject>();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(REPOSITORY_BASE_PATH, path))) {
                SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
                // Calendar cal = Calendar.getInstance();
                for (Path pathObj : directoryStream) {
                    BasicFileAttributes attrs = Files.readAttributes(pathObj, BasicFileAttributes.class);

                    if (onlyFolders && !attrs.isDirectory()) {
                        continue;
                    }
                    JSONObject el = new JSONObject();
                    el.put("name", pathObj.getFileName().toString());
                    //el.put("rights", getPermissions(pathObj));
                    el.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
                    el.put("size", attrs.size());
                    el.put("type", attrs.isDirectory() ? "dir" : "file");
                    resultList.add(el);
                }
            } catch (IOException ex) {
            }

            return new JSONObject().put("result", resultList);
        } catch (Exception e) {
            LOG.error("list", e);
            return error(e.getMessage());
        }
    }

    private JSONObject rename(JSONObject params) throws ServletException {
        try {
            String path = params.getString("path");
            String newpath = params.getString("newPath");
            LOG.debug("rename from: {} to: {}", path, newpath);

            File srcFile = new File(REPOSITORY_BASE_PATH, path);
            File destFile = new File(REPOSITORY_BASE_PATH, newpath);
            if (srcFile.isFile()) {
                FileUtils.moveFile(srcFile, destFile);
            } else {
                FileUtils.moveDirectory(srcFile, destFile);
            }
            return success(params);
        } catch (Exception e) {
            LOG.error("rename", e);
            return error(e.getMessage());
        }
    }

    private JSONObject copy(JSONObject params) throws ServletException {
        try {
            String path = params.getString("path");
            String newpath = params.getString("newPath");
            LOG.debug("copy from: {} to: {}", path, newpath);
            File srcFile = new File(REPOSITORY_BASE_PATH, path);
            File destFile = new File(REPOSITORY_BASE_PATH, newpath);
            if (srcFile.isFile()) {
                FileUtils.copyFile(srcFile, destFile);
            } else {
                FileUtils.copyDirectory(srcFile, destFile);
            }
            return success(params);
        } catch (Exception e) {
            LOG.error("copy", e);
            return error(e.getMessage());
        }
    }

    private JSONObject delete(JSONObject params) throws ServletException {
        try {
            String path = params.getString("path");
            LOG.debug("delete {}", path);
            File srcFile = new File(REPOSITORY_BASE_PATH, path);
            if (!FileUtils.deleteQuietly(srcFile)) {
                throw new Exception("Can't delete: " + srcFile.getAbsolutePath());
            }
            return success(params);
        } catch (Exception e) {
            LOG.error("delete", e);
            return error(e.getMessage());
        }
    }

    private JSONObject editFile(JSONObject params) throws ServletException {
        // getDBProfile content
        try {
            String path = params.getString("path");
            LOG.debug("editFile path: {}", path);

            File srcFile = new File(REPOSITORY_BASE_PATH, path);
            String content = FileUtils.readFileToString(srcFile);

            return new JSONObject().put("result", content);
        } catch (Exception e) {
            LOG.error("editFile", e);
            return error(e.getMessage());
        }
    }

    private JSONObject saveFile(JSONObject params) throws ServletException {
        // save content
        try {
            String path = params.getString("path");
            String content = params.getString("content");
            LOG.debug("saveFile path: {} content: isNotBlank {}, size {}", path, StringUtils.isNotBlank(content), content != null ? content.length() : 0);

            File srcFile = new File(REPOSITORY_BASE_PATH, path);
            FileUtils.writeStringToFile(srcFile, content);

            return success(params);
        } catch (Exception e) {
            LOG.error("saveFile", e);
            return error(e.getMessage());
        }
    }

    private JSONObject addFolder(JSONObject params) throws ServletException {
        try {
            String path = params.getString("newPath");
            String name = params.getString("newPath");
            if (params.has("name")) {
                name = params.getString("name");
            }
            LOG.debug("addFolder path: {} name: {}", path, name);

            File newDir = new File(REPOSITORY_BASE_PATH, name);
            if (!newDir.mkdir()) {
                throw new Exception("Can't create directory: " + newDir.getAbsolutePath());
            }
            return success(params);
        } catch (Exception e) {
            LOG.error("addFolder", e);
            return error(e.getMessage());
        }
    }

    private JSONObject changePermissions(JSONObject params) throws ServletException {
        try {
            String path = params.getString("path");
            String perms = params.getString("perms"); // "653"
            String permsCode = params.getString("permsCode"); // "rw-r-x-wx"
            boolean recursive = params.getBoolean("recursive");
            LOG.debug("changePermissions path: {} perms: {} permsCode: {} recursive: {}", path, perms, permsCode, recursive);
            File f = new File(REPOSITORY_BASE_PATH, path);
            setPermissions(f, permsCode, recursive);
            return success(params);
        } catch (Exception e) {
            LOG.error("changePermissions", e);
            return error(e.getMessage());
        }
    }

    private JSONObject compress(JSONObject params) throws ServletException {
        try {
            String path = params.getString("path"); // "/public_html/compressed.zip"
            String destination = params.getString("destination"); // "/public_html/backups"
            // FIXME parameters are right? the doc so...
            LOG.debug("compress path: {} destination: {}", path, destination);
            // TODO
            return error("not implemented");
            // return success(params);
        } catch (Exception e) {
            LOG.error("compress", e);
            return error(e.getMessage());
        }
    }

    private JSONObject extract(JSONObject params) throws ServletException {
        try {
            String path = params.getString("path"); // "/public_html/compressed.zip"
            String destination = params.getString("destination"); // "/public_html/extracted-files"
            String sourceFile = params.getString("item"); // /public_html/compressed.zip"
            // FIXME parameters are right? the doc so...
            LOG.debug("extract path: {} destination: {} sourceFile: {}", path, destination, sourceFile);
            // TODO
            return error("not implemented");
        } catch (Exception e) {
            LOG.error("extract", e);
            return error(e.getMessage());
        }
    }

    private String getPermissions(Path path) throws IOException {
        // http://www.programcreek.com/java-api-examples/index.php?api=java.nio.file.attribute.PosixFileAttributes
        PosixFileAttributes readAttributes = Files.readAttributes(path, PosixFileAttributes.class);
        Set<PosixFilePermission> permissions = readAttributes.permissions();
        return PosixFilePermissions.toString(permissions);
    }

    private String setPermissions(File file, String permsCode, boolean recursive) throws IOException {
        // http://www.programcreek.com/java-api-examples/index.php?api=java.nio.file.attribute.PosixFileAttributes
        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
        fileAttributeView.setPermissions(PosixFilePermissions.fromString(permsCode));
        if (file.isDirectory() && recursive && file.listFiles() != null) {
            for (File f : file.listFiles()) {
                setPermissions(f, permsCode, recursive);
            }
        }
        return permsCode;
    }

    private JSONObject error(String msg) throws ServletException {
        try {
            // { "result": { "success": false, "error": "msg" } }
            JSONObject result = new JSONObject();
            result.put("success", false);
            result.put("error", msg);
            return new JSONObject().put("result", result);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private JSONObject success(JSONObject params) throws ServletException {
        try {
            // { "result": { "success": true, "error": null } }
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("error", (Object) null);
            return new JSONObject().put("result", result);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }
}
