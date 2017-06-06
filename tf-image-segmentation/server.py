from http.server import BaseHTTPRequestHandler, HTTPServer
import subprocess as sp
import shutil
PORT_NUMBER = 3123
crlf = "\\r\\n\\r\\n"

TEMP_FILE = '/home/niravraham/workspace/saper-project/input_file.txt'
NEW_IMG_LOCATION = '/home/niravraham/workspace/saper-project/new_input_img.jpeg'
MAIN_FCN_FILE = '/home/niravraham/workspace/saper-project/tensorflow-deeplab-resnet/inference.py '
ANALYZED_IMG_LOC = '/home/niravraham/workspace/saper-project/tf-image-segmentation/output/new_input_img_result.jpeg'
ANALYZED_TEST_PIC = '/home/niravraham/workspace/saper-project/tf-image-segmentation/output/cat_result.jpg'
TEST_IMAGE = '/home/niravraham/workspace/saper-project/cat.jpeg'

class ServerHandler(BaseHTTPRequestHandler):

    @staticmethod
    def execute(cmd):
        popen = sp.Popen(cmd, stdout=sp.PIPE, universal_newlines=True, shell=True)
        for stdout_line in iter(popen.stdout.readline, ""):
            yield stdout_line
        popen.stdout.close()
        return_code = popen.wait()
        if return_code:
            raise sp.CalledProcessError(return_code, cmd)

    # Handler for the POST requests
    def do_POST(self):
        try:
            send_reply = True
            mime_type = ''
            self.data_string = self.rfile.read(int(self.headers['Content-Length']))
            print("got post request...")
            f = open(TEMP_FILE, 'wb')
            f.write(self.data_string)
            f.close()
            new_file = open(NEW_IMG_LOCATION, 'wb')
            with open(TEMP_FILE, 'rb') as nw:
                lines = nw.readlines()[4:-5]
                new_file.writelines(lines)
            new_file.close()

            # new_img_loc = analyze_image('new_input_img.jpg')
            # run fcn algorithim
            for path in self.execute("python " + MAIN_FCN_FILE + NEW_IMG_LOCATION):
                print (path)
            # sp.check_output("python \"" + MAIN_FCN_FILE + "\"")
            new_file = open(ANALYZED_IMG_LOC , 'rb')

            self.send_response(200)
            self.send_header('Content-type', 'text/plain')
            # self.send_header("Content-Length", str(len(str_to_ret)))
            self.end_headers()
            self.wfile.write(new_file.read())
            new_file.close()
            return
        except Exception as e:
            self.send_error(404, e)

    def do_GET(self):
        try:
            
            # new_img_loc = analyze_image('new_input_img.jpg')
            # run fcn algorithim
            for path in self.execute("python " + MAIN_FCN_FILE + TEST_IMAGE):
                print (path)
            # sp.check_output("python \"" + MAIN_FCN_FILE + "\"")
            # new_file = open(ANALYZED_IMG_LOC , 'rb')

            self.send_response(200)
            self.send_header('Content-type', 'image/jpeg')
            # self.send_header("Content-Length", str(len(str_to_ret)))
            self.end_headers()
            with open(ANALYZED_TEST_PIC, 'rb') as content:
                shutil.copyfileobj(content, self.wfile)           
            # new_file.close()
            return
        except Exception as e:
            self.send_error(404, e)

if __name__ == "__main__":
    try:

        server = HTTPServer(('', PORT_NUMBER), ServerHandler)
        print('Server Running on port ', PORT_NUMBER)
        server.serve_forever()

    except KeyboardInterrupt:
        print('^C received, shutting down the web server')
        server.socket.close()
