from __future__ import division
import time
import os
import sys
import tensorflow as tf
import skimage.io as io
import matplotlib.pyplot as plt
import numpy as np
import server
sys.path.append("/home/niravraham/workspace/saper-project/models/slim/")
fcn_16s_checkpoint_path = '/home/niravraham/workspace/saper-project/fcn_16s_checkpoint/model_fcn16s_final.ckpt'
fcn_8s_checkpoint_path = '/home/niravraham/workspace/saper-project/fcn_8s_checkpoint/model_fcn8s_final.ckpt'
os.environ["CUDA_VISIBLE_DEVICES"] = '1'
slim = tf.contrib.slim
from tf_image_segmentation.models.fcn_8s import FCN_8s as FCN_8
from tf_image_segmentation.utils.inference import adapt_network_for_any_size_input
number_of_classes = 21


def analyze_image(file_loc):

    image_filename = file_loc

    image_filename_placeholder = tf.placeholder(tf.string)

    feed_dict_to_use = {image_filename_placeholder: image_filename}

    image_tensor = tf.read_file(image_filename_placeholder)

    image_tensor = tf.image.decode_jpeg(image_tensor, channels=3)

    # Fake batch for image and annotation by adding
    # leading empty axis.
    image_batch_tensor = tf.expand_dims(image_tensor, axis=0)

    # Be careful: after adaptation, network returns final labels
    # and not logits
    FCN_8s = adapt_network_for_any_size_input(FCN_8, 32)

    pred, fcn_16s_variables_mapping = FCN_8s(image_batch_tensor=image_batch_tensor,
                                             number_of_classes=number_of_classes,
                                             is_training=False)

    # The op for initializing the variables.
    initializer = tf.local_variables_initializer()

    saver = tf.train.Saver()

    with tf.Session() as sess:
        t0 = time.time()
        sess.run(initializer)

        saver.restore(sess, fcn_8s_checkpoint_path)
        t1 = time.time()
        print("Part1: " + str(t1 - t0))
        t0 = time.time()

        image_np, pred_np = sess.run([image_tensor, pred], feed_dict=feed_dict_to_use)
        #
        # io.imshow(image_np)
        # io.show()
        # io.imshow(pred_np.squeeze())
        # io.show()
        cut_img = pred_np.squeeze()
        complete = np.copy(image_np)
        t1 = time.time()
        print("Part2: " + str(t1 - t0))
        for row in range(cut_img.shape[0]):
            for col in range(cut_img.shape[1]):
                if cut_img[row, col] == 0:
                    complete[row, col] = 0
        io.imsave(server.ANALYZED_IMG_LOC, complete)
        return server.ANALYZED_IMG_LOC


analyze_image(server.NEW_IMG_LOCATION)

# img_to_test = '/home/niravraham/Downloads/cat.jpeg'
# new_loc = analyze_image(img_to_test)
# plt.subplot(121), plt.imshow(io.imread(img_to_test), cmap='gray')
# plt.title('Original Image'), plt.xticks([]), plt.yticks([])
# plt.subplot(122), plt.imshow(io.imread(new_loc), cmap='gray')
# plt.title('cut Image'), plt.xticks([]), plt.yticks([])
# plt.show()