helloworld のサンプル
====================

はじめに
---------------------

アプリディレクトリに .md ファイルを作成すると、Markdown 形式として認識され
レンダリングされます。

index.md ファイルはいわゆる *index.html* と同様に振る舞います。

[/apps/helloworld/](/apps/helloworld/) にアクセスしたとき、index.md があればそれを表示します。

### Header 3

> This is a blockquote.
> 
> This is the second paragraph in the blockquote.
>
> ## This is an H2 in a blockquote

## Mustach

なぜ Mustach か。テンプレートの埋め込み用文字が他のスクリプトと干渉しにくく、シンプルだからです。
noobify で使用するさい約束事として、テンプレートとなるファイル全体を {{parameterized}} で囲んでください:

> {{#parameterized}}
>
> #!/bin/sh
> 
> JAVA_HOME={{JAVA_HOME}}
> JAVA_CMD=$JAVA_HOME/bin/java
> 
> {{/parameterized}}

[ Mustach ](http://mustache.github.io/)

