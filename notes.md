# NOTES

We have simutaneous capture of the left and right audio channels working now.
The next step is to improve this code to facilitate the chunking of the audio
snippets so we can send discrete packets to the server for storage.

Probably in our MicCaptureRunnable2 class, we can read chunks from the mic
as before but do something to switch the output buffer dynamically so that we 
can flush the current buffer to disk, with the smallest possible delay before
starting to write the audio frames to the new buffer. 

Buffers that have been flushed to disk can sit in a "pending" directory until the
uploader job picks them up and pushes them to the server. Once a saved buffer has
been uploaded we can maybe keep it for a short period of time in case there was any
issue with the upload, then we can reap it from disk.

See, for reference:

https://stackoverflow.com/questions/7266042/ring-buffer-in-java

https://en.wikipedia.org/wiki/Circular_buffer


