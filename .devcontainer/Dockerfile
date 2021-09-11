FROM gradle:6.9.1-jdk8

ENV SDKMAN_DIR /usr/local/sdkman
ENV KOTLIN_PATH ${SDKMAN_DIR}/candidates/kotlin/current/

RUN set -x \
    && apt-get update \
    && apt-get install -y unzip zip --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

COPY .bashrc /root/.bashrc

RUN curl -s get.sdkman.io | bash

RUN set -x \
    && echo "sdkman_auto_answer=true" > $SDKMAN_DIR/etc/config \
    && echo "sdkman_auto_selfupdate=false" >> $SDKMAN_DIR/etc/config \
    && echo "sdkman_insecure_ssl=false" >> $SDKMAN_DIR/etc/config \
    && chmod a+x "$SDKMAN_DIR/bin/sdkman-init.sh" \
    && echo "alias kts='java -jar $KOTLIN_PATH/lib/kotlin-compiler.jar -script'" >> /root/.bashrc

RUN ["/bin/bash", "-c", "source $SDKMAN_DIR/bin/sdkman-init.sh && sdk install kotlin"]
