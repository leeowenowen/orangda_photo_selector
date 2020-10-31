import 'package:flutter/material.dart';

class PhotoSelectorView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: AndroidView(
        viewType: 'com.orangda.post_page.PostPage',
        // creationParams: {'text': 'Flutter传给Android的参数'},
        // creationParamsCodec: StandardMessageCodec(),
      ),
    );
  }
}
