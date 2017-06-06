import time
import subprocess as sp
# NEW_IMG_LOCATION = '/home/niravraham/workspace/saper-project/new_input_img.jpg'
MAIN_FCN_FILE = 'python /home/niravraham/workspace/saper-project/tensorflow-deeplab-resnet/inference.py '
# ANALYZED_IMG_LOC = '/home/niravraham/workspace/saper-project/tf-image-segmentation/output/new_input_img_result.jpg'
# ANALYZED_TEST_PIC = '/home/niravraham/workspace/saper-project/tf-image-segmentation/output/cat_result.jpg'
img_base = ' /home/niravraham/workspace/saper-project/images/'
ending = '.jpeg'
images = []

img_c_base = ' /home/niravraham/workspace/saper-project/images-compress/'
ending = '.jpeg'
images_c = []

overall_time_reg = 0
# for img in images:
# 	t0 = time.time()
# 	popen = sp.Popen(MAIN_FCN_FILE + img, shell=True)
# 	return_code = popen.wait()
# 	print return_code
# 	t1 = time.time()
# 	print "Time for img: " + img + ". " + str(t1-t0)
# 	overall_time_reg +=  (t1 -t0)

# print "Time for all images: " + str(overall_time_reg)

num_of_images = 3
overall_time_com = 0
for i in range(1, num_of_images + 1):
	images_c.append(img_c_base + str(i) + ending)
for img in images_c:
	t0 = time.time()
	popen = sp.Popen(MAIN_FCN_FILE + img, shell=True)
	return_code = popen.wait()
	print return_code
	t1 = time.time()
	print "Time for img_c: " + img + ". " + str(t1-t0)
	overall_time_com +=  (t1 -t0)

print "Time for all images: " + str(overall_time_com)

