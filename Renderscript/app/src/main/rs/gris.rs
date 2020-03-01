#pragma version (1)
#pragma rs java_package_name ( com . android . rssample )
#pragma rs_fp_full
static const float4 weight = {0.299f,0.587f,0.114f,0.0f};
float minimum=255;
float maximum=0;
uchar4 RS_KERNEL toGray ( uchar4 in ) {
const float4 pixelf = rsUnpackColor8888 (in) ;
const float gray = dot ( pixelf , weight ) ;
return rsPackColorTo8888 (gray,gray,gray,pixelf.a) ;
}

void RS_KERNEL maxmin ( uchar4 in ) {
const float4 pixelf = rsUnpackColor8888 (in) ;
float gray =pixelf.r;
if (pixelf.r>maximum){
    maximum = gray;
    }
if (pixelf.r<minimum){
    minimum= gray;
    }
}

uchar4 RS_KERNEL contrast ( uchar4 in ) {
const float4 pixelf = rsUnpackColor8888 (in) ;
rsPackColorTo8888 (255*(pixelf.b-minimum)/(maximum-minimum),255*(pixelf.b-minimum)/(maximum-minimum),255*(pixelf.b-minimum)/(maximum-minimum),pixelf.a) ;
}